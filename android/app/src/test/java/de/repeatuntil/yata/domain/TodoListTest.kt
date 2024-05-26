package de.repeatuntil.yata.domain

import de.repeatuntil.yata.domain.todolist.TodoList
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import org.junit.Test

class TodoListTest {

    @Test
    fun `new to-do list has a random unique id`() {
        // Given
        val todoList1 = TodoList()
        val todoList2 = TodoList()

        // Then
        todoList1.id shouldNotBeEqual todoList2.id
    }

    @Test
    fun `new to-do list has no user`() {
        // Given
        val todoList = TodoList()

        // Then
        todoList.userId.shouldBeNull()
    }

    @Test
    fun `new to-do list has no to-dos`() {
        // Given
        val todoList = TodoList()

        // Then
        todoList.todos.shouldBeEmpty()
    }
}