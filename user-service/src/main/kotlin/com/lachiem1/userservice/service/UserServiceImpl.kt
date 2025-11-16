package com.lachiem1.userservice.service

import com.lachiem1.userservice.domain.user.UserClient
import com.lachiem1.userservice.domain.user.UserRepositoryResult
import com.lachiem1.userservice.domain.user.UserServer
import com.lachiem1.userservice.domain.user.UserServiceResult
import com.lachiem1.userservice.repository.UserRepository

class UserServiceImpl(private val repository: UserRepository): UserService {
    override suspend fun validateUserRequestInformation(user: UserClient): UserServiceResult {
        return when (val result = repository.getUserByUsername(user.username)) {
            is UserRepositoryResult.SuccessReturnUserById -> UserServiceResult.SuccessNoContent
            is UserRepositoryResult.DuplicateUsernameError -> UserServiceResult.UsernameAlreadyTakenError
            is UserRepositoryResult.DuplicateEmailError -> UserServiceResult.EmailAlreadyTakenError
            is UserRepositoryResult.UnexpectedException -> UserServiceResult.DownstreamSystemError(result.exception)
            else -> {UserServiceResult.DownstreamSystemError(null)}
        }
    }

    override suspend fun validateUserExists(id: String): UserServiceResult {
        return when (val result = repository.getUserById(id)) {
            is UserRepositoryResult.SuccessReturnUserById -> UserServiceResult.SuccessNoContent
            is UserRepositoryResult.UserNotFoundError -> UserServiceResult.NotFoundError
            is UserRepositoryResult.UnexpectedException -> UserServiceResult.DownstreamSystemError(result.exception)
            else -> {UserServiceResult.DownstreamSystemError(null)}
        }
    }

    override suspend fun createUser(user: UserClient): UserServiceResult {
        val validateUserResponse = validateUserRequestInformation(user)
        if (validateUserResponse !is UserServiceResult.SuccessNoContent)
            return validateUserResponse

        return when (val createUserResponse = repository.createUser(user)) {
            is UserRepositoryResult.UnexpectedException -> UserServiceResult.DownstreamSystemError(createUserResponse.exception)
            else -> {UserServiceResult.SuccessNoContent}
        }
    }

    override suspend fun readUserById(id: String): UserServiceResult {
        return when (val result = repository.getUserById(id)) {
            is UserRepositoryResult.SuccessReturnUserById -> UserServiceResult.SuccessReturnUserById(result.userById)
            is UserRepositoryResult.UnexpectedException -> UserServiceResult.DownstreamSystemError(result.exception)
            else -> {UserServiceResult.DownstreamSystemError(null)}
        }
    }

    override suspend fun readAllUsers(): UserServiceResult {
//        val response = repository.getAllUsers()
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: UserServer): UserServiceResult {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(user: UserServer): UserServiceResult {
        TODO("Not yet implemented")
    }
}
