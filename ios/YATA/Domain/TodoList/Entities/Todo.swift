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
    let isCompleted: Bool

    var hasDeadline: Bool {
        deadline != Deadline.farFuture
    }

    init(id: Id = Id(),
         title: String = "",
         description: String = "",
         deadline: Deadline = Deadline.farFuture,
         isCompleted: Bool = false) {
        self.id = id
        self.title = title
        self.description = description
        self.deadline = deadline
        self.isCompleted = isCompleted
    }

    func removeDeadline() -> Todo {
        return Todo(id: id, title: title, description: description, deadline: Deadline.farFuture)
    }

}

extension Todo: Equatable {

    static func == (lhs: Todo, rhs: Todo) -> Bool {
        return lhs.id == rhs.id &&
        lhs.title == rhs.title &&
        lhs.description == rhs.description &&
        lhs.deadline == rhs.deadline &&
        lhs.isCompleted == rhs.isCompleted
    }
}

extension Todo: Hashable {

    func hash(into hasher: inout Hasher) {
        hasher.combine(id)
        hasher.combine(title)
        hasher.combine(description)
        hasher.combine(deadline)
        hasher.combine(isCompleted)
    }
}
