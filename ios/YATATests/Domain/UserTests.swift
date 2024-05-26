//
//  UserTests.swift
//  YATATests
//
//  Created by Alpár Szotyori on 20.05.24.
//

import XCTest
@testable import YATA

final class UserTests: XCTestCase {

    func testNewUserHasRandomUniqueId() throws {
        // Given
        let user1 = User()
        let user2 = User()

        // Then
        XCTAssertTrue(user1.id != user2.id)
    }

    func testNewUserHasNoTodoList() throws {
        // Given
        let user = User()

        // Then
        XCTAssertNil(user.todoListId)
    }

}
