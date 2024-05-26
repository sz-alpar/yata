package de.repeatuntil.yata.domain

import de.repeatuntil.yata.domain.common.Id
import de.repeatuntil.yata.domain.user.User
import de.repeatuntil.yata.domain.user.UserRepository
import de.repeatuntil.yata.domain.user.UserService
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldMatchInOrder
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UserServiceTest {

    @Test
    fun `gets active user`() = runTest {
        // Given
        val repository = InMemoryUserRepository()
        repository.addUser(User(id = Id("1"), isActive = false))
        repository.addUser(User(id = Id("2"), isActive = false))
        repository.addUser(User(id = Id("3"), isActive = true))

        val userService = UserService(repository)

        // When
        val activeUser = userService.getActiveUser().getOrThrow()

        // Then
        activeUser.shouldNotBeNull()
            .id.shouldNotBeNull().shouldBeEqual(Id("3"))
    }

    @Test
    fun `adding active user deactivates previous user`() = runTest {
        // Given
        val repository = InMemoryUserRepository()
        repository.addUser(User(id = Id("1"), isActive = true))
        repository.addUser(User(id = Id("2"), isActive = false))
        repository.addUser(User(id = Id("3"), isActive = false))

        val userService = UserService(repository)

        // When
        userService.addUser(User(id = Id("4"), isActive = true))

        // Then
        val users = userService.getAllUsers().getOrThrow()
        users.shouldMatchInOrder(
            { it.isActive.shouldBeFalse() },
            { it.isActive.shouldBeFalse() },
            { it.isActive.shouldBeFalse() },
            { it.isActive.shouldBeTrue() }
        )
    }

    @Test
    fun `making user active deactivates previous user`() = runTest {
        // Given
        val repository = InMemoryUserRepository()
        repository.addUser(User(id = Id("1"), isActive = true))
        repository.addUser(User(id = Id("2"), isActive = false))
        val user = User(id = Id("3"), isActive = false)
        repository.addUser(user)

        val userService = UserService(repository)

        // When
        userService.updateUser(user.copy(isActive = true))

        // Then
        val users = userService.getAllUsers().getOrThrow()
        users.shouldMatchInOrder(
            { it.isActive.shouldBeFalse() },
            { it.isActive.shouldBeFalse() },
            { it.isActive.shouldBeTrue() }
        )
    }

}

class InMemoryUserRepository : UserRepository {


    private val users: MutableList<User> = mutableListOf()

    override suspend fun findActiveUser(): Result<User?> {
        return Result.success(users.firstOrNull { it.isActive })
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        return Result.success(users)
    }

    override suspend fun updateUsers(users: List<User>): Result<Unit> {
        users.forEach { updateUser(it) }
        return Result.success(Unit)
    }

    override suspend fun addUser(user: User): Result<Unit> {
        users.add(user)
        return Result.success(Unit)
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        val index = users.indexOfFirst { it.id == user.id }
        if (index >= 0) {
            users[index] = user
        }
        return Result.success(Unit)
    }
}