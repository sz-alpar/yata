//
//  User.swift
//  YATA
//
//  Created by Alpár Szotyori on 20.05.24.
//

import Foundation

struct User {
    let id: Id
    let todoList: TodoList

    init(id: Id = Id(), todoList: TodoList = TodoList()) {
        self.id = id
        self.todoList = todoList
    }
}
