//
//  User.swift
//  YATA
//
//  Created by Alpár Szotyori on 20.05.24.
//

import Foundation

struct User {
    let id: Id
    let todoListId: Id?
    let isActive: Bool

    init(id: Id = Id(),
         todoListId: Id? = nil,
         isActive: Bool = false) {
        self.id = id
        self.todoListId = todoListId
        self.isActive = isActive
    }

    func copy(id: Id? = nil,
              todoListId: Id? = nil,
              isActive: Bool? = nil) -> User {
        return User(id: id ?? self.id,
                    todoListId: todoListId ?? self.todoListId,
                    isActive: isActive ?? self.isActive)
    }
}
