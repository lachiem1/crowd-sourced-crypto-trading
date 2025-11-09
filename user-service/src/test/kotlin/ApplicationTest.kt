package com.lachiem1.userservice

import com.lachiem1.userservice.model.UserClient
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testGetUser() = testApplication {
        application { module() }

        val response: HttpResponse = client.get("/user")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testCreateUser() = testApplication {
        application { module() }

        // Create a test client with JSON serialization installed
        val jsonClient = createClient {
            install(ContentNegotiation) { json() }
        }

        val res = jsonClient.post("/user") {
            contentType(ContentType.Application.Json)
            setBody(UserClient(username = "john", email = "john@example.com"))
        }
        assertEquals(HttpStatusCode.Created, res.status)
    }
}
