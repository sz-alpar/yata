package de.repeatuntil.yata.data.todolist

import de.repeatuntil.yata.domain.todolist.TodoList
import de.repeatuntil.yata.domain.user.User

interface TodoListDataSource {

    suspend fun getTodoListForUser(user: User): TodoList?

    suspend fun addTodoList(todoList: TodoList)

    suspend fun updateTodoList(todoList: TodoList)

}