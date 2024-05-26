package de.repeatuntil.yata.infrastructure.todolist

import de.repeatuntil.yata.domain.common.Id
import de.repeatuntil.yata.domain.todolist.TodoList
import de.repeatuntil.yata.domain.todolist.TodoListRepository

class TodoListMemoryRepository : TodoListRepository {

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