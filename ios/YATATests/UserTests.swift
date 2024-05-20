//
//  UserTests.swift
//  YATATests
//
//  Created by Alpár Szotyori on 20.05.24.
//

import XCTest
@testable import YATA

final class UserTests: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testNewUserHasRandomUniqueId() throws {
        // Given
        let user1 = User()
        let user2 = User()

        // Then
        XCTAssertTrue(user1.id != user2.id)
    }

    func testNewUserHasEmptyTodoList() throws {
        // Given
        let user = User()

        // Then
        XCTAssertTrue(user.todoList.todos.isEmpty)
    }

}
