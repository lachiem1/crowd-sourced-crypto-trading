package com.lachiem1.userservice.service

import com.lachiem1.userservice.domain.user.UserClient
import com.lachiem1.userservice.domain.user.UserServer
import com.lachiem1.userservice.domain.user.UserServiceResult

interface UserService {
    suspend fun validateUserRequestInformation(user: UserClient) : UserServiceResult
    suspend fun validateUserExists(id: String) : UserServiceResult
    suspend fun createUser(user: UserClient) : UserServiceResult
    suspend fun readUserById(id: String) : UserServiceResult
    suspend fun readAllUsers() : UserServiceResult
    suspend fun updateUser(user: UserServer) : UserServiceResult
    suspend fun deleteUser(user: UserServer) : UserServiceResult
}