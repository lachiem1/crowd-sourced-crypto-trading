package com.lachiem1.userservice.model

import kotlinx.serialization.Serializable

@Serializable
data class UserServer(
    val id: String,
    val username: String,
    val email: String
)

@Serializable
data class UserClient(
    val username: String,
    val email: String
)