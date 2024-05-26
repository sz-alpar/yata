package de.repeatuntil.yata.domain.user

import de.repeatuntil.yata.domain.common.DomainException
import de.repeatuntil.yata.domain.common.mapFailureToRepositoryException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserService(
    private val repository: UserRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getActiveUser(): Result<User?> {
        return withContext(ioDispatcher) {
            repository.findActiveUser()
                .mapFailureToRepositoryException()
        }
    }

    suspend fun getAllUsers(): Result<List<User>> {
        return withContext(ioDispatcher) {
            repository.getAllUsers()
                .mapFailureToRepositoryException()
        }
    }

    suspend fun addUser(user: User): Result<Unit> {
        return withContext(ioDispatcher) {
            if (user.isActive) {
                addActiveUser(user)
            } else {
                repository.addUser(user)
                    .mapFailureToRepositoryException()
            }
        }
    }

    private suspend fun addActiveUser(activeUser: User): Result<Unit> {
        assert(activeUser.isActive)
        return withContext(ioDispatcher) {
            deactivateOtherUsersForActiveUser(activeUser) { repository.addUser(it) }
        }
    }

    private suspend fun updateActiveUser(activeUser: User): Result<Unit> {
        assert(activeUser.isActive)
        return withContext(ioDispatcher) {
            deactivateOtherUsersForActiveUser(activeUser) { repository.updateUser(it) }
        }
    }

    private suspend fun deactivateOtherUsersForActiveUser(
        activeUser: User,
        actionForActiveUser: suspend (User) -> Result<Unit>
    ): Result<Unit> {
        assert(activeUser.isActive)
        return withContext(ioDispatcher) {
            repository.getAllUsers()
                .map { users ->
                    // Deactivate all other users
                    users.mapNotNull { if (it.id != activeUser.id) it.copy(isActive = false) else null }
                }
                // Update users
                .fold(
                    onSuccess = { repository.updateUsers(it) },
                    onFailure = { Result.failure(DomainException.RepositoryException(it)) }
                )
                // Run action for user
                .fold(
                    onSuccess = { actionForActiveUser(activeUser) },
                    onFailure = { Result.failure(DomainException.RepositoryException(it)) }
                )
                .mapFailureToRepositoryException()
        }
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return withContext(ioDispatcher) {
            if (user.isActive) {
                updateActiveUser(user)
            } else {
                repository.updateUser(user)
                    .mapFailureToRepositoryException()
            }
        }
    }
}
