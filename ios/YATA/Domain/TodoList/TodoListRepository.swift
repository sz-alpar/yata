//
//  TodoListRepository.swift
//  YATA
//
//  Created by Alpár Szotyori on 26.05.24.
//

import Foundation

protocol TodoListRepository {
    func getTodoListForUser(_ userId: Id) async -> Result<TodoList?, Error>

    func addTodoList(_ todoList: TodoList) async -> Result<Void, Error>

    func updateTodoList(_ todoList: TodoList) async -> Result<Void, Error>
}
