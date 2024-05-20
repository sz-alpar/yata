//
//  TodoTests.swift
//  YATATests
//
//  Created by Alpár Szotyori on 20.05.24.
//

import XCTest
@testable import YATA

final class TodoTests: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testNewTodoHasRandomUniqueId() throws {
        // Given
        let todo1 = Todo()
        let todo2 = Todo()

        // Then
        XCTAssertTrue(todo1.id != todo2.id)
    }

    func testNewTodoHasNoDeadline() throws {
        // Given
        let todo = Todo()

        // Then
        XCTAssertFalse(todo.hasDeadline())
    }

    func testNewTodoHasEmptyTitleAndDescription() throws {
        // Given
        let todo = Todo()

        // Then
        XCTAssertEqual(todo.title, "")
        XCTAssertEqual(todo.description, "")
    }

    func testRemoveDeadline() throws {
        // Given
        let todo = Todo(deadline: Deadline.now())

        // When
        XCTAssertTrue(todo.hasDeadline())
        let todoWithoutDeadline = todo.removeDeadline()

        // Then
        XCTAssertFalse(todoWithoutDeadline.hasDeadline())
    }

    func testIsOverdueWhenDeadlineHasPassed() throws {
        // Given
        let now = Deadline.now()
        let yesterday = Calendar.current.date(byAdding: .day, value: -1, to: now.date)!
        let deadlineYesterday = Deadline(date: yesterday, timeZone: now.timeZone)

        let todo = Todo(deadline: deadlineYesterday)

        // Then
        XCTAssertTrue(todo.deadline.isOverdue())
    }

    func testIsOverdueWhenDeadlineIsNow() throws {
        // Given
        let now = Deadline.now()

        let todo = Todo(deadline: now)

        // Then
        XCTAssertTrue(todo.deadline.isOverdue(now.date))
    }

    func testIsOverdueWhenCurrentTimeZoneIsAheadOfDeadlineTimeZone() throws {
        // Given
        let dateNow = Date.now

        let deadlineTimeZone = TimeZone(identifier: "UTC+1")!
        let deadlineDate = Date(timeInterval: TimeInterval(deadlineTimeZone.secondsFromGMT(for: dateNow)),
                                since: dateNow)

        let todo = Todo(deadline: Deadline(date: deadlineDate, timeZone: deadlineTimeZone))

        // When
        let currentTimeZone = TimeZone(identifier: "UTC+2")!
        let currentDate = Date(timeInterval: TimeInterval(currentTimeZone.secondsFromGMT(for: dateNow)),
                               since: dateNow)

        // Then
        XCTAssertTrue(todo.deadline.isOverdue(currentDate))
    }

}
