//
//  TodoListApplicationService.swift
//  YATA
//
//  Created by Alpár Szotyori on 26.05.24.
//

import Foundation

class TodoListApplicationService {

    let todoListService: TodoListService
    let userService: UserService

    init(todoListService: TodoListService, userService: UserService) {
        self.todoListService = todoListService
        self.userService = userService
    }

    func loadTodoListForActiveUser() async -> Result<TodoList?, TodoListApplicationServiceError> {
        switch await userService.getActiveUser() {
        case .success(let activeUser):
            guard let activeUser else {
                return Result.failure(.noActiveUser)
            }
            return await todoListService.loadTodoListForUser(activeUser.id)
                .wrapServiceError()
        case .failure(let error):
            return Result.failure(.userServiceError(error))
        }
    }

    func loadOrCreateTodoListForActiveUser() async -> Result<TodoList, TodoListApplicationServiceError> {
        switch await userService.getActiveUser() {
        case .success(let activeUser):
            guard let activeUser else {
                return Result.failure(.noActiveUser)
            }

            switch await todoListService.loadTodoListForUser(activeUser.id) {
            case .success(let todoList):
                if let todoList {
                    return Result.success(todoList)
                } else {
                    // Create a new todo list for the user
                    let newTodoList = TodoList(userId: activeUser.id)
                    switch await todoListService.addTodoList(newTodoList) {
                    case .success:
                        // Update the user to reference the new todo list
                        let updatedUser = activeUser.copy(todoListId: newTodoList.id)
                        return await userService.updateUser(updatedUser)
                            .map { newTodoList }
                            .wrapServiceError()
                    case .failure(let error):
                        return Result.failure(.todoListServiceError(error))
                    }
                }
            case .failure(let error):
                return Result.failure(.todoListServiceError(error))
            }
        case .failure(let error):
            return Result.failure(.userServiceError(error))
        }
    }

    func updateTodoListForActiveUser(_ todoList: TodoList) async -> Result<Void, TodoListApplicationServiceError> {
        switch await userService.getActiveUser() {
        case .success(let activeUser):
            guard let activeUser else {
                return Result.failure(.noActiveUser)
            }
            return await todoListService.updateTodoListForUser(todoList, userId: activeUser.id)
                .wrapServiceError()
        case .failure(let error):
            return Result.failure(.userServiceError(error))
        }
    }

}

enum TodoListApplicationServiceError: Error {
    case userServiceError(_ error: UserServiceError)
    case todoListServiceError(_ error: TodoListServiceError)
    case noActiveUser
}

fileprivate extension Result where Failure == UserServiceError {

    func wrapServiceError() -> Result<Success, TodoListApplicationServiceError> {
        return mapError { error in
            .userServiceError(error)
        }
    }
}

fileprivate extension Result where Failure == TodoListServiceError {

    func wrapServiceError() -> Result<Success, TodoListApplicationServiceError> {
        return mapError { error in
            .todoListServiceError(error)
        }
    }
}
