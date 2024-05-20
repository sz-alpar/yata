package de.repeatuntil.yata.data

import app.cash.turbine.turbineScope
import de.repeatuntil.yata.domain.common.Id
import de.repeatuntil.yata.domain.todolist.TodoList
import de.repeatuntil.yata.domain.todolist.entities.Todo
import de.repeatuntil.yata.data.todolist.TodoListDataSource
import de.repeatuntil.yata.data.todolist.TodoListRepository
import de.repeatuntil.yata.domain.user.User
import de.repeatuntil.yata.data.user.UserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TodoListRepositoryTest {

    @Test
    fun `if user has no to-do list, then creates to-do list for user when loading`() = runTest {
        // Given
        val dataSource = InMemoryTodoListDataSource()

        val userDataSource = InMemoryUserDataSource()
        val user = User(id = Id("1"), isActive = true)
            .also { userDataSource.addUser(it) }
        val userRepository = UserRepository(userDataSource)

        val todoListRepository = TodoListRepository(dataSource, userRepository)

        // When
        launch {
            todoListRepository.loadTodoListForUser(user)
        }

        // Then
        turbineScope {
            val todoListFlow = todoListRepository.currentTodoListFlow.testIn(backgroundScope)
            val activeUserFlow = userRepository.activeUserFlow.testIn(backgroundScope)

            val todoList = todoListFlow.awaitItem()
            assertEquals(user.id, todoList?.userId)

            val activeUser = activeUserFlow.awaitItem()
            assertEquals(todoList?.id, activeUser?.todoListId)
        }
    }

    @Test
    fun `updates to-do list for user`() = runTest {
        // Given
        val userDataSource = InMemoryUserDataSource()
        val user = User(id = Id("1"), isActive = true)
            .also { userDataSource.addUser(it) }
        val userRepository = UserRepository(userDataSource)

        val dataSource = InMemoryTodoListDataSource()
        val todoList = TodoList(userId = user.id)
        dataSource.addTodoList(todoList)
        val todoListRepository = TodoListRepository(dataSource, userRepository)

        // When
        val updatedTodoList = todoList.copy(todos = mutableListOf(Todo(Id("1"), "Do something")))
        todoListRepository.updateTodoListForUser(updatedTodoList, user)

        // Then
        val todoListForUser = dataSource.getTodoListForUser(user)
        assertEquals(updatedTodoList, todoListForUser)
    }

    @Test
    fun `doesn't update to-do list for other user`() = runTest {
        // Given
        val userDataSource = InMemoryUserDataSource()
        val user = User(id = Id("1"), isActive = true)
            .also { userDataSource.addUser(it) }
        val userRepository = UserRepository(userDataSource)

        val dataSource = InMemoryTodoListDataSource()
        val todoList = TodoList(userId = user.id)
        dataSource.addTodoList(todoList)
        val todoListRepository = TodoListRepository(dataSource, userRepository)

        // When
        val updatedTodoList = todoList.copy(todos = mutableListOf(Todo(Id("1"), "Do something")))

        var exception: IllegalArgumentException? = null
        try {
            todoListRepository.updateTodoListForUser(updatedTodoList, User(id = Id("2")))
        } catch (e: IllegalArgumentException) {
            exception = e
        }

        // Then
        assertNotNull(exception)
    }

}

class InMemoryTodoListDataSource : TodoListDataSource {

    private var todoLists = mutableListOf<TodoList>()

    override suspend fun getTodoListForUser(user: User): TodoList? {
        return todoLists.firstOrNull { it.userId == user.id }
    }

    override suspend fun addTodoList(todoList: TodoList) {
        todoLists.add(todoList)
    }

    override suspend fun updateTodoList(todoList: TodoList) {
        val index = todoLists.indexOfFirst { it.id == todoList.id }
        if (index >= 0) {
            todoLists[index] = todoList
        }
    }

}