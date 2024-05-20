package de.repeatuntil.yata.domain.todolist

import de.repeatuntil.yata.domain.common.Id
import de.repeatuntil.yata.domain.todolist.entities.Todo

data class TodoList(
    val id: Id = Id(),
    val todos: List<Todo> = emptyList()
)