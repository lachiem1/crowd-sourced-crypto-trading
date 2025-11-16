package com.lachiem1.userservice.repository

import com.lachiem1.userservice.domain.user.UserClient
import com.lachiem1.userservice.domain.user.UserRepositoryResult
import com.lachiem1.userservice.domain.user.UserServer

interface UserRepository {
    suspend fun getAllUsers(): UserRepositoryResult
    suspend fun getUserById(id: String): UserRepositoryResult
    suspend fun getUserByUsername(username: String): UserRepositoryResult
    suspend fun getUserByEmail(email: String): UserRepositoryResult
    suspend fun createUser(user: UserClient): UserRepositoryResult
    suspend fun updateUser(user: UserServer): UserRepositoryResult
    suspend fun deleteUser(id: String): UserRepositoryResult
}