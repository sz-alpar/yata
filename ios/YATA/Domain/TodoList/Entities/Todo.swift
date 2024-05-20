//
//  Todo.swift
//  YATA
//
//  Created by Alpár Szotyori on 20.05.24.
//

import Foundation

struct Todo {
    let id: Id
    let title: String
    let description: String
    let deadline: Deadline

    init(id: Id = Id(), title: String = "", description: String = "", deadline: Deadline = Deadline.farFuture) {
        self.id = id
        self.title = title
        self.description = description
        self.deadline = deadline
    }

    func hasDeadline() -> Bool {
        return deadline != Deadline.farFuture
    }

    func removeDeadline() -> Todo {
        return Todo(id: id, title: title, description: description, deadline: Deadline.farFuture)
    }

}
