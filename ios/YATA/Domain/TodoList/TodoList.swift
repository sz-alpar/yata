//
//  TodoList.swift
//  YATA
//
//  Created by Alpár Szotyori on 20.05.24.
//

import Foundation

struct TodoList {
    let id: Id
    let todos: [Todo]

    init(id: Id = Id(), todos: [Todo] = []) {
        self.id = id
        self.todos = todos
    }
}
