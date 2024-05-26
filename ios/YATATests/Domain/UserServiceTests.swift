//
//  UserServiceTests.swift
//  YATATests
//
//  Created by Alpár Szotyori on 26.05.24.
//

import XCTest
@testable import YATA

final class UserServiceTests: XCTestCase {

    func testGetActiveUser() async throws {
        // Given
        let repository = InMemoryUserRepository()
        _ = repository.addUser(User(id: Id("1"), isActive: false))
        _ = repository.addUser(User(id: Id("2"), isActive: false))
        _ = repository.addUser(User(id: Id("3"), isActive: true))

        let userService = UserService(repository: repository)

        // When
        let activeUser = try await userService.getActiveUser().get()

        // Then
        XCTAssertNotNil(activeUser)
        XCTAssertEqual(activeUser?.id, Id("3"))
    }

    func testAddingActiveUserDeactivatesPreviousUser() async throws {
        // Given
        let repository = InMemoryUserRepository()
        _ = repository.addUser(User(id: Id("1"), isActive: true))
        _ = repository.addUser(User(id: Id("2"), isActive: false))
        _ = repository.addUser(User(id: Id("3"), isActive: false))

        let userService = UserService(repository: repository)

        // When
        _ = try await userService.addUser(User(id: Id("4"), isActive: true)).get()

        // Then
        let users = try await userService.getAllUsers().get()
        XCTAssertFalse(users[0].isActive)
        XCTAssertFalse(users[1].isActive)
        XCTAssertFalse(users[2].isActive)
        XCTAssertTrue(users[3].isActive)
    }

    func testMakingUserActiveDeactivatesPreviousUser() async throws {
        // Given
        let repository = InMemoryUserRepository()
        _ = repository.addUser(User(id: Id("1"), isActive: true))
        _ = repository.addUser(User(id: Id("2"), isActive: false))
        let user = User(id: Id("3"), isActive: false)
        _ = repository.addUser(user)

        let userService = UserService(repository: repository)

        // When
        _ = try await userService.updateUser(user.copy(isActive: true)).get()

        // Then
        let users = try await userService.getAllUsers().get()
        XCTAssertFalse(users[0].isActive)
        XCTAssertFalse(users[1].isActive)
        XCTAssertTrue(users[2].isActive)
    }

}

final class InMemoryUserRepository: UserRepository {

    private var users: [User] = []

    func findActiveUser() -> Result<YATA.User?, any Error> {
        Result.success(users.first { user in user.isActive})
    }

    func getAllUsers() -> Result<[YATA.User], any Error> {
        Result.success(users)
    }

    func updateUsers(_ users: [YATA.User]) -> Result<Void, any Error> {
        users.forEach { user in
            _ = updateUser(user)
        }
        return Result.success(())
    }

    func addUser(_ user: YATA.User) -> Result<Void, any Error> {
        users.append(user)
        return Result.success(())
    }

    func updateUser(_ user: YATA.User) -> Result<Void, any Error> {
        if let index = users.firstIndex(where: { $0.id == user.id }) {
            users[index] = user
        }
        return Result.success(())
    }

}
