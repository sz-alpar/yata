//
//  UserMemoryRepository.swift
//  YATA
//
//  Created by Alpár Szotyori on 26.05.24.
//

import Foundation

class UserMemoryRepository: UserRepository {

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
