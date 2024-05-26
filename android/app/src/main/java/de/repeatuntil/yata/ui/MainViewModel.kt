package de.repeatuntil.yata.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.repeatuntil.yata.application.todolist.TodoListApplicationService
import de.repeatuntil.yata.application.user.UserApplicationService
import de.repeatuntil.yata.domain.todolist.TodoList
import de.repeatuntil.yata.domain.todolist.TodoListService
import de.repeatuntil.yata.domain.todolist.entities.Todo
import de.repeatuntil.yata.domain.user.UserService
import de.repeatuntil.yata.infrastructure.todolist.TodoListMemoryRepository
import de.repeatuntil.yata.infrastructure.user.UserMemoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val userService: UserService = UserService(UserMemoryRepository()),
    private val todoListApplicationService: TodoListApplicationService = TodoListApplicationService(
        TodoListService(TodoListMemoryRepository()),
        userService
    ),
    private val userApplicationService: UserApplicationService = UserApplicationService(userService)
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                runCatching {
                    val activeUser = userApplicationService.loadOrCreateActiveUser().getOrThrow()
                    val todoList = todoListApplicationService.loadOrCreateTodoListForActiveUser().getOrThrow()
                    _uiState.value = UIState.ShowTodoList(todoList)
                }.onFailure {
                    _uiState.value = UIState.ShowError(it.message ?: "Unknown error")
                }
            }
        }
    }

    fun addTodo(todoList: TodoList, todo: Todo) {
        val updatedTodoList =
            todoList.copy(todos = todoList.todos + todo)
        viewModelScope.launch {
            runCatching {
                todoListApplicationService.updateTodoListForActiveUser(updatedTodoList).getOrThrow()
                val loadedTodoList = todoListApplicationService.loadTodoListForActiveUser().getOrThrow()
                if (loadedTodoList != null) {
                    _uiState.value = UIState.ShowTodoList(loadedTodoList)
                } else {
                    _uiState.value = UIState.ShowError("Failed to add to-do: to-do list not found")
                }
            }.onFailure {
                _uiState.value = UIState.ShowError(it.message ?: "Unknown error")
            }
        }
    }

}

sealed class UIState {
    data object Loading : UIState()
    data class ShowError(val message: String) : UIState()
    data class ShowTodoList(val todoList: TodoList) : UIState()
}