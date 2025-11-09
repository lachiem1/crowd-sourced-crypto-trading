package com.lachiem1.userservice.service

import com.lachiem1.userservice.model.UserClient
import com.lachiem1.userservice.model.UserServer
import com.lachiem1.userservice.repository.UserRepository

class UserService(
    private val repository: UserRepository = UserRepository()
) {
    fun getUserSample(): UserServer = repository.getSample()
    fun createUser(request: UserClient): UserServer = repository.create(request)
}
