//
//  TodoListTests.swift
//  YATATests
//
//  Created by Alpár Szotyori on 20.05.24.
//

import XCTest
@testable import YATA

final class TodoListTests: XCTestCase {

    func testNewTodoListHasRandomUniqueId() throws {
        // Given
        let todoList1 = TodoList()
        let todoList2 = TodoList()

        // Then
        XCTAssertTrue(todoList1.id != todoList2.id)
    }

    func testNewTodoListHasNoUser() {
        // Given
        let todoList = TodoList()

        // Then
        XCTAssertNil(todoList.userId)
    }

    func testNewTodoListHasNoTodos() {
        // Given
        let todoList = TodoList()

        // Then
        XCTAssertTrue(todoList.todos.isEmpty)
    }

}
