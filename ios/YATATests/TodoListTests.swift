//
//  TodoListTests.swift
//  YATATests
//
//  Created by Alpár Szotyori on 20.05.24.
//

import XCTest
@testable import YATA

final class TodoListTests: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testNewTodoListHasRandomUniqueId() throws {
        // Given
        let todoList1 = TodoList()
        let todoList2 = TodoList()

        // Then
        XCTAssertTrue(todoList1.id != todoList2.id)
    }

    func testNewTodoListHasNoTodos() {
        // Given
        let todoList = TodoList()

        // Then
        XCTAssertTrue(todoList.todos.isEmpty)
    }

}
