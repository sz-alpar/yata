package de.repeatuntil.yata.domain.todolist

import de.repeatuntil.yata.domain.common.Id

interface TodoListRepository {

    suspend fun getTodoListForUser(userId: Id): Result<TodoList?>

    suspend fun addTodoList(todoList: TodoList): Result<Unit>

    suspend fun updateTodoList(todoList: TodoList): Result<Unit>

}