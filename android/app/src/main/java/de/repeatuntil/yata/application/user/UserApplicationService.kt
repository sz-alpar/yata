package de.repeatuntil.yata.application.user

import de.repeatuntil.yata.domain.user.User
import de.repeatuntil.yata.domain.user.UserService

class UserApplicationService(
    private val userService: UserService,
) {

    suspend fun loadOrCreateActiveUser(): Result<User> {
        return runCatching {
            val activeUser = userService.getActiveUser().getOrThrow()
            if (activeUser != null) {
                activeUser
            } else {
                val newUser = User(isActive = true)
                userService.addUser(newUser).getOrThrow()
                newUser
            }
        }
    }
}