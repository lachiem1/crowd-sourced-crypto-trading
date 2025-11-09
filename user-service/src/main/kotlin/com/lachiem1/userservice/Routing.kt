package com.lachiem1.userservice

import com.lachiem1.userservice.controller.UserController
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        val userController = UserController()
        with(userController) {
            register()
        }
    }
}
