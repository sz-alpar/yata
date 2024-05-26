//
//  TodoListService.swift
//  YATA
//
//  Created by Alpár Szotyori on 26.05.24.
//

import Foundation

class TodoListService {
    private let repository: TodoListRepository

    init(repository: TodoListRepository) {
        self.repository = repository
    }

    func addTodoList(_ todoList: TodoList) async -> Result<Void, TodoListServiceError> {
        return await repository.addTodoList(todoList)
            .mapErrorToRepositoryError()
            .wrapDomainError()
    }

    func loadTodoListForUser(_ userId: Id) async -> Result<TodoList?, TodoListServiceError> {
        return await repository.getTodoListForUser(userId)
            .mapErrorToRepositoryError()
            .wrapDomainError()
    }

    func updateTodoListForUser(_ todoList: TodoList, userId: Id) async -> Result<Void, TodoListServiceError> {
        guard todoList.userId == userId else {
            return Result.failure(.todoListDoesNotBelongToUser(userId))
        }

        let existingTodoList = try? await loadTodoListForUser(userId).get()
        if existingTodoList != nil {
            return await repository.updateTodoList(todoList)
                .mapErrorToRepositoryError()
                .wrapDomainError()
        } else {
            return Result.failure(.todoListDoesNotExistForUser(userId))
        }
    }

}

enum TodoListServiceError: Error {
    case domainError(_ error: DomainError)
    case todoListDoesNotBelongToUser(_ userId: Id)
    case todoListDoesNotExistForUser(_ userId: Id)

}

fileprivate extension Result where Failure == DomainError {

    func wrapDomainError() -> Result<Success, TodoListServiceError> {
        return mapError { error in
            .domainError(error)
        }
    }

}
