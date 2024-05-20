package de.repeatuntil.yata.data.todolist

import de.repeatuntil.yata.data.user.UserRepository
import de.repeatuntil.yata.domain.todolist.TodoList
import de.repeatuntil.yata.domain.user.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext

class TodoListRepository(
    private val dataSource: TodoListDataSource,
    private val userRepository: UserRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val _currentTodoListFlow: MutableSharedFlow<TodoList?> = MutableSharedFlow()
    val currentTodoListFlow: Flow<TodoList?> = _currentTodoListFlow.asSharedFlow()

    suspend fun loadTodoListForUser(user: User) {
        withContext(ioDispatcher) {
            val todoList = dataSource.getTodoListForUser(user) ?: run {
                val newTodoList = TodoList(userId = user.id)
                dataSource.addTodoList(newTodoList)

                val updatedUser = user.copy(todoListId = newTodoList.id)
                userRepository.updateUser(updatedUser)
                newTodoList
            }
            _currentTodoListFlow.emit(todoList)
        }
    }

    @Throws(IllegalArgumentException::class)
    suspend fun updateTodoListForUser(todoList: TodoList, user: User) {
        withContext(ioDispatcher) {
            if (todoList.userId == user.id) {
                dataSource.updateTodoList(todoList)
                _currentTodoListFlow.emit(todoList)
            } else {
                throw IllegalArgumentException("TodoList does not belong to user")
            }
        }
    }
}