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
        val userService = UserService(InMemoryUserRepository())
        val user = User(id = Id("1"))
        userService.addUser(user)

        val todoListService = TodoListService(InMemoryTodoListRepository())
        val todoList = TodoList(userId = user.id)
        todoListService.addTodoList(todoList)

        // When
        val loadedTodoList = todoListService.loadTodoListForUser(user.id).getOrThrow()

        // Then
        loadedTodoList.shouldNotBeNull()
            .shouldBeEqual(todoList)
    }

    @Test
    fun `returns null to-do list, if user has no to-do list`() = runTest {
        // Given
        val userService = UserService(InMemoryUserRepository())
        val user = User(id = Id("1"))
        userService.addUser(user)

        val todoListService = TodoListService(InMemoryTodoListRepository())

        // When
        val todoList = todoListService.loadTodoListForUser(user.id).getOrThrow()

        // Then
        todoList.shouldBeNull()
    }

    @Test
    fun `updates to-do list for user`() = runTest {
        // Given
        val userService = UserService(InMemoryUserRepository())
        val user = User(id = Id("1"))
        userService.addUser(user)

        val todoListService = TodoListService(InMemoryTodoListRepository())
        val todoList = TodoList(userId = user.id)
        todoListService.addTodoList(todoList)

        // When
        val todo = Todo(id = Id("1"), description = "Do something")
        val updatedTodoList = todoList.copy(todos = mutableListOf(todo))

        todoListService.updateTodoListForUser(updatedTodoList, user.id)

        // Then
        val loadedTodoList = todoListService.loadTodoListForUser(user.id).getOrThrow()

        loadedTodoList.shouldNotBeNull()
            .shouldBeEqual(updatedTodoList)
    }

    @Test
    fun `doesn't update to-do list for other user`() = runTest {
        // Given
        val userService = UserService(InMemoryUserRepository())
        val user1 = User(id = Id("1"))
        userService.addUser(user1)
        val user2 = User(id = Id("2"))
        userService.addUser(user2)

        val todoListService = TodoListService(InMemoryTodoListRepository())
        val todoList1 = TodoList(userId = user1.id)
        todoListService.addTodoList(todoList1)
        val todoList2 = TodoList(userId = user2.id)
        todoListService.addTodoList(todoList2)

        // When
        val todo = Todo(id = Id("1"), description = "Do something")
        val updatedTodoList2 = todoList2.copy(todos = mutableListOf(todo))

        val exception = todoListService.updateTodoListForUser(updatedTodoList2, user1.id).exceptionOrNull()

        // Then
        exception.shouldBeTypeOf<TodoListServiceException.TodoListDoesNotBelongToUser>()
    }

    @Test
    fun `returns 'to-do list does not exist for user' exception, if the user has no to-do list`() = runTest {
        // Given
        val userService = UserService(InMemoryUserRepository())
        val user = User(id = Id("1"), isActive = true)
        userService.addUser(user)

        val todoListService = TodoListService(InMemoryTodoListRepository())

        // When
        val todoList = TodoList(userId = user.id)

        val result = todoListService.updateTodoListForUser(todoList, user.id)

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