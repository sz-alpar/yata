package de.repeatuntil.yata.infrastructure.user

import de.repeatuntil.yata.domain.user.User
import de.repeatuntil.yata.domain.user.UserRepository

class UserMemoryRepository : UserRepository {

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