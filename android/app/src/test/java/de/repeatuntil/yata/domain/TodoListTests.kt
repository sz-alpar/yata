package de.repeatuntil.yata.domain

import de.repeatuntil.yata.domain.todolist.TodoList
import org.junit.Test

class TodoListTests {

    @Test
    fun `new to-do list has a random unique id`() {
        // Given
        val todoList1 = TodoList()
        val todoList2 = TodoList()

        // Then
        assert(todoList1.id != todoList2.id)
    }

    @Test
    fun `new to-do list has no to-dos`() {
        // Given
        val todoList = TodoList()

        // Then
        assert(todoList.todos.isEmpty())
    }
}