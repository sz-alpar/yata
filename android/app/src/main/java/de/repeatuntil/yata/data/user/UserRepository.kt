package de.repeatuntil.yata.data.user

import de.repeatuntil.yata.domain.user.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext

class UserRepository(
    private val dataSource: UserDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val _activeUserFlow: MutableSharedFlow<User?> = MutableSharedFlow()
    val activeUserFlow: Flow<User?> = _activeUserFlow.asSharedFlow()

    suspend fun getActiveUser(): User? {
        return withContext(ioDispatcher) {
            val activeUser = dataSource.findActiveUser()
            _activeUserFlow.emit(activeUser)
            return@withContext activeUser
        }
    }

    suspend fun getAllUsers(): List<User> {
        return withContext(ioDispatcher) {
            dataSource.getAllUsers()
        }
    }

    suspend fun addUser(user: User) {
        withContext(ioDispatcher) {
            if (user.isActive) {
                val deactivatedUsers = dataSource.getAllUsers()
                    .map { it.copy(isActive = false) }
                dataSource.updateUsers(deactivatedUsers)
            }
            dataSource.addUser(user)
            _activeUserFlow.emit(user)
        }
    }

    suspend fun removeUser(user: User) {
        withContext(ioDispatcher) {
            dataSource.removeUser(user)
            if (user.isActive) {
                _activeUserFlow.emit(null)
            }
        }
    }

    suspend fun updateUser(user: User) {
        withContext(ioDispatcher) {
            if (user.isActive) {
                val deactivatedUsers = dataSource.getAllUsers()
                    .map { it.copy(isActive = false) }
                dataSource.updateUsers(deactivatedUsers)
            }
            dataSource.updateUser(user)
            _activeUserFlow.emit(user)
        }
    }
}