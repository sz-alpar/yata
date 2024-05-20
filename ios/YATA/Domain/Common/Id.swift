//
//  Id.swift
//  YATA
//
//  Created by Alpár Szotyori on 20.05.24.
//

import Foundation

// swiftlint:disable:next type_name
struct Id: Equatable {
    let value: String

    init(id: String = UUID().uuidString) {
        self.value = id
    }

    static func == (lhs: Id, rhs: Id) -> Bool {
        return lhs.value == rhs.value
    }

}
