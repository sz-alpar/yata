package de.repeatuntil.yata.domain

import de.repeatuntil.yata.domain.common.Id
import de.repeatuntil.yata.domain.todolist.TodoList
import de.repeatuntil.yata.domain.todolist.TodoListRepository
import de.repeatuntil.yata.domain.todolist.TodoListService
import de.repeatuntil.yata.domain.todolist.TodoListServiceException
import de.repeatuntil.yata.domain.todolist.entities.Todo
import de.repeatuntil.yata.domain.user.User
import de.repeatuntil.yata.domain.user.UserService
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TodoListServiceTest {

    @Test
    fun `loads to-do list for user`() = runTest {
        // Given
        val todoListService = TodoListService(InMemoryTodoListRepository())
        val userId = Id("1")
        val todoList = TodoList(userId = userId)
        todoListService.addTodoList(todoList)

        // When
        val loadedTodoList = todoListService.loadTodoListForUser(userId).getOrThrow()

        // Then
        loadedTodoList.shouldNotBeNull()
            .shouldBeEqual(todoList)
    }

    @Test
    fun `returns null to-do list, if user has no to-do list`() = runTest {
        // Given
        val todoListService = TodoListService(InMemoryTodoListRepository())

        // When
        val todoList = todoListService.loadTodoListForUser(Id("1")).getOrThrow()

        // Then
        todoList.shouldBeNull()
    }

    @Test
    fun `updates to-do list for user`() = runTest {
        // Given
        val todoListService = TodoListService(InMemoryTodoListRepository())
        val userId = Id("1")
        val todoList = TodoList(userId = userId)
        todoListService.addTodoList(todoList)

        // When
        val todo = Todo(id = Id("1"), description = "Do something")
        val updatedTodoList = todoList.copy(todos = mutableListOf(todo))

        todoListService.updateTodoListForUser(updatedTodoList, userId)

        // Then
        val loadedTodoList = todoListService.loadTodoListForUser(userId).getOrThrow()

        loadedTodoList.shouldNotBeNull()
            .shouldBeEqual(updatedTodoList)
    }

    @Test
    fun `doesn't update to-do list for other user`() = runTest {
        // Given
        val user1Id = Id("1")
        val user2Id = Id("2")

        val todoListService = TodoListService(InMemoryTodoListRepository())
        val todoList1 = TodoList(userId = user1Id)
        todoListService.addTodoList(todoList1)
        val todoList2 = TodoList(userId = user2Id)
        todoListService.addTodoList(todoList2)

        // When
        val todo = Todo(id = Id("1"), description = "Do something")
        val updatedTodoList2 = todoList2.copy(todos = mutableListOf(todo))

        val exception = todoListService.updateTodoListForUser(updatedTodoList2, user1Id).exceptionOrNull()

        // Then
        exception.shouldBeTypeOf<TodoListServiceException.TodoListDoesNotBelongToUser>()
    }

    @Test
    fun `returns 'to-do list does not exist for user' exception, if the user has no to-do list`() = runTest {
        // Given
        val userId = Id("1")

        val todoListService = TodoListService(InMemoryTodoListRepository())

        // When
        val todoList = TodoList(userId = userId)

        val result = todoListService.updateTodoListForUser(todoList, userId)

        // Then
        result.exceptionOrNull().shouldBeTypeOf<TodoListServiceException.TodoListDoesNotExistForUser>()
    }


}

class InMemoryTodoListRepository : TodoListRepository {

    private val todoLists = mutableListOf<TodoList>()

    override suspend fun getTodoListForUser(userId: Id): Result<TodoList?> {
        return Result.success(todoLists.firstOrNull { it.userId == userId })
    }

    override suspend fun addTodoList(todoList: TodoList): Result<Unit> {
        todoLists.add(todoList)
        return Result.success(Unit)
    }

    override suspend fun updateTodoList(todoList: TodoList): Result<Unit> {
        val index = todoLists.indexOfFirst { it.id == todoList.id }
        if (index >= 0) {
            todoLists[index] = todoList
        }
        return Result.success(Unit)
    }

}