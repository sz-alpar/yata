package de.repeatuntil.yata.application

import de.repeatuntil.yata.application.user.UserApplicationService
import de.repeatuntil.yata.domain.InMemoryUserRepository
import de.repeatuntil.yata.domain.common.Id
import de.repeatuntil.yata.domain.user.User
import de.repeatuntil.yata.domain.user.UserService
import io.kotest.matchers.equals.shouldBeEqual
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UserApplicationServiceTest {

    @Test
    fun `returns active user, if it exists`() = runTest {
        // Given
        val userService = UserService(InMemoryUserRepository())
        val user = User(id = Id("1"), isActive = true)
        userService.addUser(user)

        val userApplicationService = UserApplicationService(userService)

        // When
        val activeUser = userApplicationService.loadOrCreateActiveUser().getOrThrow()

        // Then
        activeUser shouldBeEqual user
    }

    @Test
    fun `if there is no active user, then it creates one`() = runTest {
        // Given
        val userService = UserService(InMemoryUserRepository())

        val userApplicationService = UserApplicationService(userService)

        // When
        val activeUser = userApplicationService.loadOrCreateActiveUser().getOrThrow()

        // Then
        activeUser.isActive shouldBeEqual true
    }
}