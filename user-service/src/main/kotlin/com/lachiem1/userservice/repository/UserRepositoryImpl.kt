package com.lachiem1.userservice.repository

//import com.lachiem1.userservice.domain.user.UserClient
//import com.lachiem1.userservice.domain.user.UserServer
//import java.util.UUID
//
//class UserRepositoryImpl(): UserRepository {}
//    override suspend fun getAllUsers(): List<UserServer> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun getUserById(): UserServer? {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun createUser(user: UserClient): Boolean {
//        val id = UUID.randomUUID().toString()
//        val newUser = UserServer(id, user.username, user.email)
//    }
//
//    override suspend fun updateUser(user: UserClient): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun deleteUser(user: UserServer): Boolean {
//        TODO("Not yet implemented")
//    }
//
//
//    fun getSample(): UserServer = UserServer(
//        id = "1234",
//        username = "lachiem1",
//        email = "lachiem1@lachiem1.com"
//    )
//
//    fun create(request: UserClient): UserServer = UserServer(
//        id = UUID.randomUUID().toString(),
//        username = request.username,
//        email = request.email
//    )
//}
