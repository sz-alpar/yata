//
//  Deadline.swift
//  YATA
//
//  Created by Alpár Szotyori on 20.05.24.
//

import Foundation

struct Deadline: Equatable {
    let date: Date
    let timeZone: TimeZone

    func isOverdue(_ now: Date = Date.now) -> Bool {
        return now >= date
    }

    static func == (lhs: Deadline, rhs: Deadline) -> Bool {
        return lhs.date == rhs.date && lhs.timeZone == rhs.timeZone
    }

    static let farFuture = Deadline(date: Date.distantFuture, timeZone: TimeZone.current)

    static func now() -> Deadline {
        return Deadline(date: Date.now, timeZone: TimeZone.current)
    }
}

extension Deadline: Hashable {

    func hash(into hasher: inout Hasher) {
        hasher.combine(date)
        hasher.combine(timeZone)
    }
}
