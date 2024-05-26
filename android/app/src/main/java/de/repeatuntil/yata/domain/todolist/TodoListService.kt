package de.repeatuntil.yata.domain.todolist

import de.repeatuntil.yata.domain.common.Id
import de.repeatuntil.yata.domain.common.mapFailureToRepositoryException
import de.repeatuntil.yata.domain.todolist.TodoListServiceException.TodoListDoesNotBelongToUser
import de.repeatuntil.yata.domain.todolist.TodoListServiceException.TodoListDoesNotExistForUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoListService(
    private val repository: TodoListRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun loadTodoListForUser(userId: Id): Result<TodoList?> {
        return withContext(ioDispatcher) {
            repository.getTodoListForUser(userId)
                .mapFailureToRepositoryException()
        }
    }

    suspend fun addTodoList(todoList: TodoList): Result<Unit> {
        return withContext(ioDispatcher) {
            repository.addTodoList(todoList)
                .mapFailureToRepositoryException()
        }
    }

    suspend fun updateTodoListForUser(todoList: TodoList, userId: Id): Result<Unit> {
        return withContext(ioDispatcher) {
            return@withContext if (todoList.userId == userId) {
                val todoListExists = loadTodoListForUser(userId).getOrNull() != null
                if (todoListExists) {
                    repository.updateTodoList(todoList)
                        .mapFailureToRepositoryException()
                } else {
                    Result.failure(TodoListDoesNotExistForUser(userId))
                }
            } else {
                Result.failure(TodoListDoesNotBelongToUser(userId))
            }
        }
    }
}

sealed class TodoListServiceException(message: String) : Exception(message) {
    class TodoListDoesNotBelongToUser(userId: Id) : TodoListServiceException("TodoList does not belong to user $userId")
    class TodoListDoesNotExistForUser(userId: Id) : TodoListServiceException("TodoList does not exist for user $userId")
}