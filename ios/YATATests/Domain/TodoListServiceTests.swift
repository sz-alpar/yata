//
//  TodoListServiceTests.swift
//  YATATests
//
//  Created by Alpár Szotyori on 26.05.24.
//

import XCTest
@testable import YATA

final class TodoListServiceTests: XCTestCase {

    func testLoadTodoListForUser() async throws {
        // Given
        let todoListService = TodoListService(repository: InMemoryTodoListRepository())

        let userId = Id("1")
        let todoList = TodoList(userId: userId)
        _ = await todoListService.addTodoList(todoList)

        // When
        let loadedTodoList = try await todoListService.loadTodoListForUser(userId).get()

        // Then
        XCTAssertEqual(loadedTodoList, todoList)
    }

    func testReturnsNilTodoListIfUserHasNoTodoList() async throws {
        // Given
        let todoListService = TodoListService(repository: InMemoryTodoListRepository())

        // When
        let loadedTodoList = try await todoListService.loadTodoListForUser(Id("1")).get()

        // Then
        XCTAssertNil(loadedTodoList)
    }

    func testUpdatesTodoListForUser() async throws {
        // Given
        let todoListService = TodoListService(repository: InMemoryTodoListRepository())

        let userId = Id("1")
        let todoList = TodoList(userId: userId)
        _ = await todoListService.addTodoList(todoList)

        // When
        let todo = Todo(id: Id("1"), description: "Do something")
        let updatedTodoList = todoList.copy(todos: [todo])

        _ = try await todoListService.updateTodoListForUser(updatedTodoList, userId: userId).get()

        let loadedTodoList = try await todoListService.loadTodoListForUser(userId).get()

        // Then
        XCTAssertEqual(loadedTodoList, updatedTodoList)
    }

    func testDoesntUpdateTodoListForOtherUser() async throws {
        // Given
        let userId1 = Id("1")
        let userId2 = Id("2")

        let todoListService = TodoListService(repository: InMemoryTodoListRepository())

        let todoList1 = TodoList(userId: userId1)
        _ = await todoListService.addTodoList(todoList1)
        let todoList2 = TodoList(userId: userId2)
        _ = await todoListService.addTodoList(todoList2)

        // When
        let todo = Todo(id: Id("1"), description: "Do something")
        let updatedTodoList2 = todoList2.copy(todos: [todo])

        let result = await todoListService.updateTodoListForUser(updatedTodoList2, userId: userId1)

        // Then
        switch result {
        case .failure(let error):
            guard case .todoListDoesNotBelongToUser = error else {
                XCTFail("Error is not todoListDoesNotBelongToUser")
                return
            }
        case .success:
            XCTFail("Did not receive error todoListDoesNotBelongToUser")
        }
    }

    func testReturnsTodoListNotExistsForUserErrorIfUserHasNoTodoList() async throws {
        // Given
        let userId = Id("1")

        let todoListService = TodoListService(repository: InMemoryTodoListRepository())

        // When
        let todoList = TodoList(userId: userId)

        let result = await todoListService.updateTodoListForUser(todoList, userId: userId)

        // Then
        switch result {
        case .failure(let error):
            guard case .todoListDoesNotExistForUser = error else {
                XCTFail("Error is not todoListDoesNotExistForUser")
                return
            }
        case .success:
            XCTFail("Did not receive error todoListDoesNotBelongToUser")
        }
    }

}

final class InMemoryTodoListRepository: TodoListRepository {

    private var todoLists: [TodoList] = []

    func getTodoListForUser(_ userId: YATA.Id) async -> Result<YATA.TodoList?, any Error> {
        return Result.success(todoLists.first { $0.userId == userId })
    }

    func addTodoList(_ todoList: YATA.TodoList) async -> Result<Void, any Error> {
        todoLists.append(todoList)
        return Result.success(())
    }

    func updateTodoList(_ todoList: YATA.TodoList) async -> Result<Void, any Error> {
        if let index = todoLists.firstIndex(where: { $0.id == todoList.id }) {
            todoLists[index] = todoList
        }
        return Result.success(())
    }

}
