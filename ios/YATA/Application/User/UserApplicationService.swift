//
//  UserApplicationService.swift
//  YATA
//
//  Created by Alpár Szotyori on 26.05.24.
//

import Foundation

class UserApplicationService {

    let userService: UserService

    init(userService: UserService) {
        self.userService = userService
    }

    func loadOrCreateActiveUser() async -> Result<User, UserApplicationServiceError> {
        switch await userService.getActiveUser() {
        case .success(let user):
            if let user {
                return Result.success(user)
            } else {
                let newUser = User(isActive: true)
                return await userService.addUser(newUser)
                    .map { newUser }
                    .wrapServiceError()
            }
        case .failure(let error):
            return Result.failure(.userServiceError(error))
        }
    }
}

enum UserApplicationServiceError: Error {
    case userServiceError(_ error: UserServiceError)
}

fileprivate extension Result where Failure == UserServiceError {

    func wrapServiceError() -> Result<Success, UserApplicationServiceError> {
        return mapError { error in
            .userServiceError(error)
        }
    }
}
