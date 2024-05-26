//
//  ContentViewModel.swift
//  YATA
//
//  Created by Alpár Szotyori on 26.05.24.
//

import Foundation
import SwiftUI

@Observable
class ContentViewModel {
    var todos: [Todo] = []
    var error: UIError?
    var todoList: TodoList = TodoList()

    private let userApplicationService: UserApplicationService
    private let todoListApplicationService: TodoListApplicationService

    init() {
        let userService = UserService(repository: UserMemoryRepository())
        self.userApplicationService = UserApplicationService(userService: userService)

        let todoListService = TodoListService(repository: TodoListMemoryRepository())
        self.todoListApplicationService = TodoListApplicationService(todoListService: todoListService,
                                                                     userService: userService)
    }

    func initialize() async {
        do {
            _ = try await userApplicationService.loadOrCreateActiveUser().get()
            self.todoList = try await todoListApplicationService.loadOrCreateTodoListForActiveUser().get()
            self.todos = self.todoList.todos
        } catch {
            self.todoList = TodoList()
            self.todos = self.todoList.todos
            self.error = .serviceError(error)
        }
    }

    func addTodo(_ todo: Todo) async {
        let updatedTodoList = todoList.addTodo(todo)
        do {
            _ = try await todoListApplicationService.updateTodoListForActiveUser(updatedTodoList).get()
            if let loadedTodoList = try await todoListApplicationService.loadTodoListForActiveUser().get() {
                self.todoList = loadedTodoList
                self.todos = loadedTodoList.todos
            } else {
                self.error = .genericError("Failed to add to-do: to-do list not found")
            }

        } catch {
            self.error = .serviceError(error)
        }
    }

    enum UIError: Error {
        case serviceError(_ error: Error)
        case genericError(_ message: String)
    }

}
