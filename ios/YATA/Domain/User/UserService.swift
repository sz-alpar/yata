//
//  UserService.swift
//  YATA
//
//  Created by Alpár Szotyori on 26.05.24.
//

import Foundation

class UserService {
    private let repository: UserRepository

    init(repository: UserRepository) {
        self.repository = repository
    }

    func getActiveUser() async -> Result<User?, UserServiceError> {
        return await repository.findActiveUser()
            .mapErrorToRepositoryError()
            .wrapDomainError()
    }

    func addUser(_ user: User) async -> Result<Void, UserServiceError> {
        if user.isActive {
            return await addActiveUser(user)
        } else {
            return await repository.addUser(user)
                .mapErrorToRepositoryError()
                .wrapDomainError()
        }
    }

    func getAllUsers() async -> Result<[User], UserServiceError> {
        return await repository.getAllUsers()
            .mapErrorToRepositoryError()
            .wrapDomainError()
    }

    func updateUser(_ user: User) async -> Result<Void, UserServiceError> {
        if user.isActive {
            return await updateActiveUser(user)
        } else {
            return await repository.updateUser(user)
                .mapErrorToRepositoryError()
                .wrapDomainError()
        }
    }
}

extension UserService {

    private func addActiveUser(_ activeUser: User) async -> Result<Void, UserServiceError> {
        assert(activeUser.isActive)
        return await deactivateOtherUsersForActiveUser(activeUser) { await repository.addUser($0) }
    }

    private func updateActiveUser(_ activeUser: User) async -> Result<Void, UserServiceError> {
        assert(activeUser.isActive)
        return await deactivateOtherUsersForActiveUser(activeUser) { await repository.updateUser($0) }
    }

    private func deactivateOtherUsersForActiveUser(_ activeUser: User,
                                                   actionForActiveUser: (User) async -> Result<Void, Error>) async
    -> Result<Void, UserServiceError> {
        assert(activeUser.isActive)

        let usersResult = await repository.getAllUsers()
            .map { users in
                users.filter { $0.id != activeUser.id }
                    .map { $0.copy(isActive: false) }
            }
        switch usersResult {
        case .success(let users):
            let updatedUsersResult = await repository.updateUsers(users)

            switch updatedUsersResult {
            case .success:
                return await actionForActiveUser(activeUser)
                    .mapErrorToRepositoryError()
                    .wrapDomainError()
            case .failure(let error):
                return Result.failure(error)
                    .mapErrorToRepositoryError()
                    .wrapDomainError()
            }
        case .failure(let error):
            return Result.failure(error)
                .mapErrorToRepositoryError()
                .wrapDomainError()
        }
    }
}

enum UserServiceError: Error {
    case domainError(_ error: DomainError)
}

fileprivate extension Result where Failure == DomainError {

    func wrapDomainError() -> Result<Success, UserServiceError> {
        return mapError { error in
            .domainError(error)
        }
    }
}
