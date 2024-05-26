package de.repeatuntil.yata.application.todolist

import de.repeatuntil.yata.application.todolist.TodoListApplicationServiceException.NoActiveUser
import de.repeatuntil.yata.domain.todolist.TodoList
import de.repeatuntil.yata.domain.todolist.TodoListService
import de.repeatuntil.yata.domain.user.UserService

class TodoListApplicationService(
    private val todoListService: TodoListService,
    private val userService: UserService
) {

    suspend fun loadTodoListForActiveUser(): Result<TodoList?> {
        return userService.getActiveUser().mapCatching { activeUser ->
            if (activeUser == null) {
                throw NoActiveUser()
            }
            todoListService.loadTodoListForUser(activeUser.id).getOrThrow()
        }
    }

    suspend fun loadOrCreateTodoListForActiveUser(): Result<TodoList> {
        return userService.getActiveUser().mapCatching { activeUser ->
            if (activeUser == null) {
                throw NoActiveUser()
            }

            val todoList = todoListService.loadTodoListForUser(activeUser.id).getOrThrow()
            if (todoList != null) {
                todoList
            } else {
                // Create a new todo list for the user
                val newTodoList = TodoList(userId = activeUser.id)
                todoListService.addTodoList(newTodoList).getOrThrow()

                // Update the user to reference the new todo list
                val updatedUser = activeUser.copy(todoListId = newTodoList.id)
                userService.updateUser(updatedUser).getOrThrow()

                newTodoList
            }
        }
    }

    suspend fun updateTodoListForActiveUser(todoList: TodoList): Result<Unit> {
        return userService.getActiveUser().mapCatching { activeUser ->
            if (activeUser == null) {
                throw NoActiveUser()
            }

            todoListService.updateTodoListForUser(todoList, activeUser.id).getOrThrow()
        }
    }

}

sealed class TodoListApplicationServiceException(message: String) : Exception(message) {
    class NoActiveUser : TodoListApplicationServiceException("No active user")
}