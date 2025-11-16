package com.lachiem1.userservice.repository

import com.lachiem1.userservice.domain.user.*
import java.util.UUID

class InMemoryUserRepository(val mockUsers: MutableList<UserServer>): UserRepository {
    override suspend fun getAllUsers(): UserRepositoryResult {
        return try {
            UserRepositoryResult.SuccessReturnAllUsers(mockUsers)
        } catch (e: Throwable) {
            UserRepositoryResult.UnexpectedException(e)
        }
    }

    override suspend fun getUserById(id: String): UserRepositoryResult {
        return try {
            val userById = mockUsers.find { it.id == id } ?: return UserRepositoryResult.UserNotFoundError
            return UserRepositoryResult.SuccessReturnUserById(userById)
        } catch (e: Throwable) {
            UserRepositoryResult.UnexpectedException(e)
        }
    }

    override suspend fun getUserByUsername(username: String): UserRepositoryResult {
        return try {
            val userByUsername = mockUsers.find { it.username == username } ?: return UserRepositoryResult.UserNotFoundError
            UserRepositoryResult.SuccessReturnUserById(userByUsername)
        } catch (e: Throwable) {
            UserRepositoryResult.UnexpectedException(e)
        }
    }

    override suspend fun getUserByEmail(email: String): UserRepositoryResult {
        return try {
            val userByEmail = mockUsers.find { it.email == email } ?: return UserRepositoryResult.UserNotFoundError
            UserRepositoryResult.SuccessReturnUserById(userByEmail)
        } catch (e: Throwable) {
            UserRepositoryResult.UnexpectedException(e)
        }
    }

    override suspend fun createUser(user: UserClient): UserRepositoryResult {
        try {
            val newUuid = UUID.randomUUID().toString()
            val newUser = UserServer(newUuid, user.username, user.email)
            mockUsers.add(newUser)
            return UserRepositoryResult.SuccessNoContent
        } catch (e: Throwable) {
            return UserRepositoryResult.UnexpectedException(e)
        }
    }

    override suspend fun updateUser(user: UserServer): UserRepositoryResult {
        try {
            val userToUpdate = when (val result = getUserById(user.id)) {
                is UserRepositoryResult.SuccessReturnUserById -> result.userById
                is UserRepositoryResult.UnexpectedException -> return UserRepositoryResult.UnexpectedException(result.exception)
                else -> {return UserRepositoryResult.UnexpectedException(null)}
            }

            val i = mockUsers.indexOfFirst { it.id == userToUpdate.id }
            mockUsers[i] = userToUpdate
            return UserRepositoryResult.SuccessNoContent
        } catch (e: Throwable) {
            return UserRepositoryResult.UnexpectedException(e)
        }
    }

    override suspend fun deleteUser(id: String): UserRepositoryResult {
        return try {
            mockUsers.removeIf { it.id == id}
            return UserRepositoryResult.SuccessNoContent
        } catch (e: Throwable) {
            UserRepositoryResult.UnexpectedException(e)
        }
    }
}