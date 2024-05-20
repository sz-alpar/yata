package de.repeatuntil.yata.data

import app.cash.turbine.test
import de.repeatuntil.yata.domain.common.Id
import de.repeatuntil.yata.domain.user.User
import de.repeatuntil.yata.data.user.UserDataSource
import de.repeatuntil.yata.data.user.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest {

    @Test
    fun `gets active user`() = runTest {
        // Given
        val dataSource = InMemoryUserDataSource()
        dataSource.addUser(User(id = Id("1"), isActive = false))
        dataSource.addUser(User(id = Id("2"), isActive = false))
        dataSource.addUser(User(id = Id("3"), isActive = true))

        val userRepository = UserRepository(dataSource)

        // When
        val activeUser = userRepository.getActiveUser()

        // Then
        assertEquals(Id("3"), activeUser?.id)
    }

    @Test
    fun `emits active user`() = runTest {
        // Given
        val dataSource = InMemoryUserDataSource()
        dataSource.addUser(User(id = Id("1"), isActive = false))
        dataSource.addUser(User(id = Id("2"), isActive = false))
        dataSource.addUser(User(id = Id("3"), isActive = true))

        val userRepository = UserRepository(dataSource)

        userRepository.activeUserFlow.test {
            // When
            userRepository.getActiveUser()

            // Then
            val activeUser = this.awaitItem()
            assertEquals(Id("3"), activeUser?.id)
        }
    }

    @Test
    fun `adding active user deactivates previous user`() = runTest {
        // Given
        val dataSource = InMemoryUserDataSource()
        dataSource.addUser(User(id = Id("1"), isActive = true))
        dataSource.addUser(User(id = Id("2"), isActive = false))
        dataSource.addUser(User(id = Id("3"), isActive = false))

        val userRepository = UserRepository(dataSource)

        // When
        userRepository.addUser(User(id = Id("4"), isActive = true))

        // Then
        val users = userRepository.getAllUsers()
        assertFalse(users[0].isActive)
        assertFalse(users[1].isActive)
        assertFalse(users[2].isActive)
        assertTrue(users[3].isActive)
    }

    @Test
    fun `adding active user emits it on flow`() = runTest {
        // Given
        val dataSource = InMemoryUserDataSource()
        dataSource.addUser(User(id = Id("1"), isActive = true))
        dataSource.addUser(User(id = Id("2"), isActive = false))
        dataSource.addUser(User(id = Id("3"), isActive = false))

        val userRepository = UserRepository(dataSource)

        userRepository.activeUserFlow.test {
            // When
            userRepository.addUser(User(id = Id("4"), isActive = true))

            // Then
            val activeUser = this.awaitItem()
            assertEquals(Id("4"), activeUser?.id)
        }
    }

    @Test
    fun `making user active deactivates previous user`() = runTest {
        // Given
        val dataSource = InMemoryUserDataSource()
        dataSource.addUser(User(id = Id("1"), isActive = true))
        dataSource.addUser(User(id = Id("2"), isActive = false))
        val user = User(id = Id("3"), isActive = false)
        dataSource.addUser(user)

        val userRepository = UserRepository(dataSource)

        // When
        userRepository.updateUser(user.copy(isActive = true))

        // Then
        val users = userRepository.getAllUsers()
        assertFalse(users[0].isActive)
        assertFalse(users[1].isActive)
        assertTrue(users[2].isActive)
    }

    @Test
    fun `making user active emits it on flow`() = runTest {
        // Given
        val dataSource = InMemoryUserDataSource()
        dataSource.addUser(User(id = Id("1"), isActive = true))
        dataSource.addUser(User(id = Id("2"), isActive = false))
        val user = User(id = Id("3"), isActive = false)
        dataSource.addUser(user)

        val userRepository = UserRepository(dataSource)

        userRepository.activeUserFlow.test {
            // When
            userRepository.updateUser(user.copy(isActive = true))

            // Then
            val activeUser = this.awaitItem()
            assertEquals(Id("3"), activeUser?.id)
        }
    }

    @Test
    fun `removing active user emits null on flow`() = runTest {
        // Given
        val dataSource = InMemoryUserDataSource()
        dataSource.addUser(User(id = Id("1"), isActive = false))
        dataSource.addUser(User(id = Id("2"), isActive = false))
        val user = User(id = Id("3"), isActive = true)
        dataSource.addUser(user)

        val userRepository = UserRepository(dataSource)

        userRepository.activeUserFlow.test {
            // When
            userRepository.removeUser(user)

            // Then
            val activeUser = this.awaitItem()
            assertNull(activeUser)
        }
    }
}

class InMemoryUserDataSource: UserDataSource {

    private var users = mutableListOf<User>()

    override suspend fun findActiveUser(): User? {
        return users.firstOrNull { it.isActive }
    }

    override suspend fun getAllUsers(): List<User> {
        return users
    }

    override suspend fun updateUsers(users: List<User>) {
        users.forEach { user ->
            updateUser(user)
        }
    }

    override suspend fun addUser(user: User) {
        users.add(user)
    }

    override suspend fun removeUser(user: User) {
        users.remove(user)
    }

    override suspend fun updateUser(user: User) {
        val index = users.indexOfFirst { it.id == user.id }
        if (index >= 0) {
            this.users[index] = user
        }
    }

}