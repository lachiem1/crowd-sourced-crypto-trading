package com.lachiem1.userservice.repository

import com.lachiem1.userservice.model.UserClient
import com.lachiem1.userservice.model.UserServer
import java.util.UUID

class UserRepository {
    fun getSample(): UserServer = UserServer(
        id = "1234",
        username = "lachiem1",
        email = "lachiem1@lachiem1.com"
    )

    fun create(request: UserClient): UserServer = UserServer(
        id = UUID.randomUUID().toString(),
        username = request.username,
        email = request.email
    )
}
