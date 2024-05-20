package de.repeatuntil.yata.data.user

import de.repeatuntil.yata.domain.user.User

interface UserDataSource {

    suspend fun findActiveUser(): User?

    suspend fun getAllUsers(): List<User>

    suspend fun updateUsers(users: List<User>)

    suspend fun addUser(user: User)

    suspend fun removeUser(user: User)

    suspend fun updateUser(user: User)
}