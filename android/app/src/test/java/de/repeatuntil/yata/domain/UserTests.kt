package de.repeatuntil.yata.domain

import de.repeatuntil.yata.domain.user.User
import org.junit.Test

class UserTests {

    @Test
    fun `new user has a random unique id`() {
        // Given
        val user1 = User()
        val user2 = User()

        // Then
        assert(user1.id != user2.id)
    }

    @Test
    fun `new user has empty todo list`() {
        // Given
        val user = User()

        // Then
        assert(user.todoList.todos.isEmpty())
    }
}