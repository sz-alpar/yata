package de.repeatuntil.yata.domain

import de.repeatuntil.yata.domain.user.User
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import org.junit.Test

class UserTests {

    @Test
    fun `new user has a random unique id`() {
        // Given
        val user1 = User()
        val user2 = User()

        // Then
        user1.id shouldNotBeEqual user2.id
    }

    @Test
    fun `new user has no todo list`() {
        // Given
        val user = User()

        // Then
        user.todoListId.shouldBeNull()
    }

    @Test
    fun `new user is not active`() {
        // Given
        val user = User()

        // Then
        user.isActive.shouldBeFalse()
    }
}