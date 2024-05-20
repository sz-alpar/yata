package de.repeatuntil.yata.data.user

import de.repeatuntil.yata.domain.user.User

class UserLocalDataSource: UserDataSource {

    override suspend fun findActiveUser(): User? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllUsers(): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUsers(users: List<User>) {
        TODO("Not yet implemented")
    }

    override suspend fun addUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun removeUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: User) {
        TODO("Not yet implemented")
    }

}
