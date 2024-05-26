//
//  TodoListMemoryRepository.swift
//  YATA
//
//  Created by Alpár Szotyori on 26.05.24.
//

import Foundation

class TodoListMemoryRepository: TodoListRepository {

    private var todoLists: [TodoList] = []

    func getTodoListForUser(_ userId: YATA.Id) async -> Result<YATA.TodoList?, any Error> {
        return Result.success(todoLists.first { $0.userId == userId })
    }

    func addTodoList(_ todoList: YATA.TodoList) async -> Result<Void, any Error> {
        todoLists.append(todoList)
        return Result.success(())
    }

    func updateTodoList(_ todoList: YATA.TodoList) async -> Result<Void, any Error> {
        if let index = todoLists.firstIndex(where: { $0.id == todoList.id }) {
            todoLists[index] = todoList
        }
        return Result.success(())
    }

}
