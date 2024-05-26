package de.repeatuntil.yata.domain.user

interface UserRepository {

    suspend fun findActiveUser(): Result<User?>

    suspend fun getAllUsers(): Result<List<User>>

    suspend fun updateUsers(users: List<User>): Result<Unit>

    suspend fun addUser(user: User): Result<Unit>

    suspend fun updateUser(user: User): Result<Unit>
}