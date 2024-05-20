package de.repeatuntil.yata.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.repeatuntil.yata.data.todolist.TodoListMemoryDataSource
import de.repeatuntil.yata.data.todolist.TodoListRepository
import de.repeatuntil.yata.data.user.UserMemoryDataSource
import de.repeatuntil.yata.data.user.UserRepository
import de.repeatuntil.yata.domain.common.Id
import de.repeatuntil.yata.domain.todolist.TodoList
import de.repeatuntil.yata.domain.todolist.entities.Todo
import de.repeatuntil.yata.domain.user.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository = UserRepository(UserMemoryDataSource()),
    private val todoListRepository: TodoListRepository = TodoListRepository(TodoListMemoryDataSource(), userRepository)
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                todoListRepository.currentTodoListFlow.collect { todoList ->
                    if (todoList != null) {
                        _uiState.value = UIState.ShowTodoList(todoList)
                    }
                }
            }

            userRepository.getActiveUser()?.let { activeUser ->
                todoListRepository.loadTodoListForUser(activeUser)
            }
        }
    }

    fun addTodo(todoList: TodoList, todo: Todo) {
        val updatedTodoList =
            todoList.copy(todos = todoList.todos + todo)
        viewModelScope.launch {
            userRepository.getActiveUser()?.let { activeUser ->
                try {
                    todoListRepository.updateTodoListForUser(updatedTodoList, activeUser)
                } catch (e: IllegalArgumentException) {
                    _uiState.emit(UIState.ShowError("TodoList does not belong to user"))
                }
            } ?: run {
                _uiState.emit(UIState.ShowError("No active user"))
            }
        }
    }

}

sealed class UIState {
    data object Loading : UIState()
    data class ShowError(val message: String) : UIState()
    data class ShowTodoList(val todoList: TodoList) : UIState()
}