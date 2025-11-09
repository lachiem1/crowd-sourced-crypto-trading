package com.lachiem1.userservice.controller

import com.lachiem1.userservice.model.UserClient
import com.lachiem1.userservice.model.UserServer
import com.lachiem1.userservice.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class UserController(private val userService: UserService = UserService()) {
    fun Route.register() {
        route("/user") {
            get {
                val user = userService.getUserSample()
                call.respond(user)
            }

            post {
                val userClient = call.receive<UserClient>()
                val created = userService.createUser(userClient)
                call.respond(HttpStatusCode.Created, created)
            }
        }
    }
}
