package de.repeatuntil.yata.application

import de.repeatuntil.yata.application.todolist.TodoListApplicationService
import de.repeatuntil.yata.application.todolist.TodoListApplicationServiceException
import de.repeatuntil.yata.domain.InMemoryTodoListRepository
import de.repeatuntil.yata.domain.InMemoryUserRepository
import de.repeatuntil.yata.domain.common.Id
import de.repeatuntil.yata.domain.todolist.TodoList
import de.repeatuntil.yata.domain.todolist.TodoListService
import de.repeatuntil.yata.domain.todolist.TodoListServiceException
import de.repeatuntil.yata.domain.todolist.entities.Todo
import de.repeatuntil.yata.domain.user.User
import de.repeatuntil.yata.domain.user.UserService
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TodoListApplicationServiceTest {

    //region loadTodoListForActiveUser()

    @Test
    fun `loads to-do list for active user`() = runTest {
        // Given
        val userService = UserService(InMemoryUserRepository())
        userService.addUser(User(id = Id("1"), isActive = true))

        val todoListService = TodoListService(InMemoryTodoListRepository())
        val todoList = TodoList(userId = userService.getActiveUser().getOrThrow()?.id)
        todoListService.addTodoList(todoList)

        val todoListApplicationService = TodoListApplicationService(todoListService, userService)

        // When
        val loadedTodoList = todoListApplicationService.loadTodoListForActiveUser().getOrThrow()

        // Then
        loadedTodoList.shouldNotBeNull()
            .shouldBeEqual(todoList)
    }

    @Test
    fun `returns null to-do list for active user, if the user has no to-do list`() = runTest {
        // Given
        val userService = UserService(InMemoryUserRepository())
        userService.addUser(User(id = Id("1"), isActive = true))

        val todoListService = TodoListService(InMemoryTodoListRepository())

        val todoListApplicationService = TodoListApplicationService(todoListService, userService)

        // When
        val loadedTodoList = todoListApplicationService.loadTodoListForActiveUser().getOrThrow()

        // Then
        loadedTodoList.shouldBeNull()
    }

    @Test
    fun `returns 'no active user' exception, if there is no active user when loading the to-do list for the active user`() =
        runTest {
            // Given
            val userService = UserService(InMemoryUserRepository())
            val todoListService = TodoListService(InMemoryTodoListRepository())
            val todoListApplicationService = TodoListApplicationService(todoListService, userService)

            // When
            val exception = todoListApplicationService.loadTodoListForActiveUser().exceptionOrNull()

            // Then
            exception.shouldNotBeNull()
                .shouldBeTypeOf<TodoListApplicationServiceException.NoActiveUser>()
        }

    //endregion

    //region loadOrCreateTodoListForActiveUser()

    @Test
    fun `returns to-do list for active user when loading`() = runTest {
        // Given
        val userService = UserService(InMemoryUserRepository())
        val activeUser = User(id = Id("1"), isActive = true)
        userService.addUser(activeUser)

        val todoListService = TodoListService(InMemoryTodoListRepository())
        val todoList = TodoList(userId = activeUser.id)
        todoListService.addTodoList(todoList)

        val todoListApplicationService = TodoListApplicationService(todoListService, userService)

        // When
        val loadedTodoList = todoListApplicationService.loadOrCreateTodoListForActiveUser().getOrThrow()

        // Then
        todoList shouldBeEqual loadedTodoList
    }

    @Test
    fun `if user has no to-do list, then creates to-do list for user when loading`() = runTest {
        // Given
        val userService = UserService(InMemoryUserRepository())
        val activeUser = User(id = Id("1"), isActive = true)
        userService.addUser(activeUser)

        val todoListService = TodoListService(InMemoryTodoListRepository())

        val todoListApplicationService = TodoListApplicationService(todoListService, userService)

        // When
        val todoList = todoListApplicationService.loadOrCreateTodoListForActiveUser().getOrThrow()

        // Then
        with(todoList.shouldNotBeNull()) {
            todos.shouldBeEmpty()
            userId.shouldNotBeNull()
                .shouldBeEqual(activeUser.id)
        }
    }

    @Test
    fun `returns 'no active user' exception, if there is no active user when loading or creating the to-do list for the active user`() =
        runTest {
            // Given
            val userService = UserService(InMemoryUserRepository())

            val todoListService = TodoListService(InMemoryTodoListRepository())

            val todoListApplicationService = TodoListApplicationService(todoListService, userService)

            // When
            val exception = todoListApplicationService.loadOrCreateTodoListForActiveUser().exceptionOrNull()

            // Then
            exception.shouldBeTypeOf<TodoListApplicationServiceException.NoActiveUser>()
        }

    //endregion

    //region updateTodoListForActiveUser()

    @Test
    fun `updates to-do list for active user`() = runTest {
        // Given
        val userService = UserService(InMemoryUserRepository())
        val activeUser = User(id = Id("1"), isActive = true)
        userService.addUser(activeUser)

        val todoListService = TodoListService(InMemoryTodoListRepository())
        val todoList = TodoList(userId = activeUser.id)
        todoListService.addTodoList(todoList)

        val todoListApplicationService = TodoListApplicationService(todoListService, userService)

        // When
        val todo = Todo(id = Id("1"), description = "Do something")
        val updatedTodoList = todoList.copy(todos = mutableListOf(todo))

        todoListApplicationService.updateTodoListForActiveUser(updatedTodoList)

        // Then
        val loadedTodoList = todoListApplicationService.loadTodoListForActiveUser().getOrThrow()

        loadedTodoList.shouldNotBeNull()
            .shouldBeEqual(updatedTodoList)
    }

    @Test
    fun `doesn't update to-do list for non-active user`() = runTest {
        // Given
        val userService = UserService(InMemoryUserRepository())
        val activeUser = User(id = Id("1"), isActive = true)
        userService.addUser(activeUser)
        val otherUser = User(id = Id("2"), isActive = false)
        userService.addUser(otherUser)

        val todoListService = TodoListService(InMemoryTodoListRepository())
        val todoList = TodoList(userId = activeUser.id)
        todoListService.addTodoList(todoList)
        val otherTodoList = TodoList(userId = otherUser.id)
        todoListService.addTodoList(otherTodoList)

        val todoListApplicationService = TodoListApplicationService(todoListService, userService)

        // When
        val todo = Todo(id = Id("1"), description = "Do something")
        val updatedOtherTodoList = otherTodoList.copy(todos = mutableListOf(todo))

        val exception = todoListApplicationService.updateTodoListForActiveUser(updatedOtherTodoList).exceptionOrNull()

        // Then
        exception.shouldBeTypeOf<TodoListServiceException.TodoListDoesNotBelongToUser>()
    }

    @Test
    fun `returns 'no active user' exception, if there is no active user when updating to-do list`() = runTest {
        // Given
        val userService = UserService(InMemoryUserRepository())
        val user = User(id = Id("2"), isActive = false)
        userService.addUser(user)

        val todoListService = TodoListService(InMemoryTodoListRepository())
        val todoList = TodoList(userId = user.id)
        todoListService.addTodoList(todoList)

        val todoListApplicationService = TodoListApplicationService(todoListService, userService)

        // When
        val todo = Todo(id = Id("1"), description = "Do something")
        val updatedTodoList = todoList.copy(todos = mutableListOf(todo))

        val exception = todoListApplicationService.updateTodoListForActiveUser(updatedTodoList).exceptionOrNull()

        // Then
        exception.shouldBeTypeOf<TodoListApplicationServiceException.NoActiveUser>()
    }

    @Test
    fun `returns 'to-do list does not exist for user' exception, if the active user has no to-do list`() = runTest {
        // Given
        val userService = UserService(InMemoryUserRepository())
        val user = User(id = Id("1"), isActive = true)
        userService.addUser(user)

        val todoListService = TodoListService(InMemoryTodoListRepository())

        val todoListApplicationService = TodoListApplicationService(todoListService, userService)

        // When
        val todoList = TodoList(userId = user.id)

        val result = todoListApplicationService.updateTodoListForActiveUser(todoList)

        // Then
        result.exceptionOrNull().shouldBeTypeOf<TodoListServiceException.TodoListDoesNotExistForUser>()
    }

    //endregion

}
