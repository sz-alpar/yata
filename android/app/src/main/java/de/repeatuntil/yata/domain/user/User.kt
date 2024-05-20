package de.repeatuntil.yata.domain.user

import de.repeatuntil.yata.domain.common.Id
import de.repeatuntil.yata.domain.todolist.TodoList

data class User(
    val id: Id = Id(),
    val todoList: TodoList = TodoList()
)