package de.repeatuntil.yata.domain

import de.repeatuntil.yata.domain.user.User
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserTests {

    @Test
    fun `new user has a random unique id`() {
        // Given
        val user1 = User()
        val user2 = User()

        // Then
        assertTrue(user1.id != user2.id)
    }

    @Test
    fun `new user has no todo list`() {
        // Given
        val user = User()

        // Then
        assertNull(user.todoListId)
    }

    @Test
    fun `new user is not active`() {
        // Given
        val user = User()

        // Then
        assertFalse(user.isActive)
    }
}