package de.repeatuntil.yata.domain.todolist.entities

import de.repeatuntil.yata.domain.common.Id
import de.repeatuntil.yata.domain.todolist.valueobjects.Deadline

data class Todo(
    val id: Id = Id(),
    val title: String = "",
    val description: String = "",
    val deadline: Deadline = Deadline.FAR_FUTURE,
    val isCompleted: Boolean = false
) {

    fun hasDeadline(): Boolean {
        return deadline != Deadline.FAR_FUTURE
    }

    fun removeDeadline(): Todo {
        return copy(deadline = Deadline.FAR_FUTURE)
    }

}