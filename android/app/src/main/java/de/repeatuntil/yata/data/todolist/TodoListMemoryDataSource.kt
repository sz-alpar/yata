package de.repeatuntil.yata.data.todolist

import de.repeatuntil.yata.domain.todolist.TodoList
import de.repeatuntil.yata.domain.user.User

class TodoListMemoryDataSource: TodoListDataSource {

    private val todoLists = mutableListOf<TodoList>()

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