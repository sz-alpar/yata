//
//  TodoList.swift
//  YATA
//
//  Created by Alpár Szotyori on 20.05.24.
//

import Foundation

struct TodoList {

    let id: Id
    let userId: Id?
    let todos: [Todo]

    init(id: Id = Id(), userId: Id? = nil, todos: [Todo] = []) {
        self.id = id
        self.userId = userId
        self.todos = todos
    }

    func copy(id: Id? = nil,
              userId: Id? = nil,
              todos: [Todo]? = nil) -> TodoList {
        return TodoList(id: id ?? self.id,
                        userId: userId ?? self.userId,
                        todos: todos ?? self.todos)
    }

    func addTodo(_ todo: Todo) -> TodoList {
        var updatedTodoList: [Todo] = []
        updatedTodoList.append(contentsOf: todos)
        updatedTodoList.append(todo)
        return copy(todos: updatedTodoList)
    }
}

extension TodoList: Equatable {

    static func == (lhs: TodoList, rhs: TodoList) -> Bool {
        return lhs.id == rhs.id &&
        lhs.userId == rhs.userId &&
        lhs.todos == rhs.todos
    }
}
