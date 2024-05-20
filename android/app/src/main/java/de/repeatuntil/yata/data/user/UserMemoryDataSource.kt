package de.repeatuntil.yata.data.user

import de.repeatuntil.yata.domain.common.Id
import de.repeatuntil.yata.domain.user.User

class UserMemoryDataSource: UserDataSource {

    private val users = mutableListOf(
        User(Id("1"), isActive = true)
    )

    override suspend fun findActiveUser(): User? {
        return users.firstOrNull { it.isActive }
    }

    override suspend fun getAllUsers(): List<User> {
        return users
    }

    override suspend fun updateUsers(users: List<User>) {
        users.forEach { updateUser(it) }
    }

    override suspend fun addUser(user: User) {
        users.add(user)
    }

    override suspend fun removeUser(user: User) {
        users.removeIf { it.id == user.id }
    }

    override suspend fun updateUser(user: User) {
        val index = users.indexOfFirst { it.id == user.id }
        if (index >= 0) {
            users[index] = user
        }
    }
}