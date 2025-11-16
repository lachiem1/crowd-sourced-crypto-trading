package com.lachiem1.userservice.repository

import com.lachiem1.userservice.domain.user.UserClient
import com.lachiem1.userservice.domain.user.UserRepositoryResult
import com.lachiem1.userservice.domain.user.UserServer
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.util.UUID

class InMemoryUserRepositoryTest : BehaviorSpec ({
    lateinit var inMemoryUserRepository: UserRepository
    lateinit var mockUsers: MutableList<UserServer>

    beforeEach {
        mockUsers = mutableListOf(
            UserServer("3c4a9af4-7b63-4c89-9d33-4c2c9e4cf4ce", "lachiem1", "lachie@lachiem1.com"),
            UserServer("a0f5ae63-0b8a-4c79-9b89-2ee6d2c1c6da", "L1nk1nP4rk", "chester@linkin.park"),
            UserServer("e7e1d932-45c1-4e8c-8e60-0b5f3f7f72d0", "123Loyle-Carner321", "loyle@msn.mail"),
            UserServer("91b7c88f-fc0a-41f7-a25d-5261c59b49f5", "RedHotChiliPeppaz", "rhcp@stadiumarcadium.usa"),
            UserServer("5c0e70f9-fd43-463e-8c8f-d4cd44d91b3e", "BlinkAndYoullMissit182", "blink@182.tour")
        )
        inMemoryUserRepository = InMemoryUserRepository(mockUsers)
    }

    Given("An in-memory data store (mutable list) is used to mock a database connection") {
        When("User service attempts to retrieve a full list of all users") {
            Then("Repository should return list of all users") {
                val getAllUsers = when (val result = inMemoryUserRepository.getAllUsers()) {
                    is UserRepositoryResult.SuccessReturnAllUsers -> result.allUsers
                    else -> {null}
                }
                getAllUsers.shouldContainExactly(mockUsers)
            }
        }

        When("User service attempts to retrieve a user by uuid") {
            When("User exists in database") {
                Then("Repository should return the user") {
                    val uuidToRetrieve = "e7e1d932-45c1-4e8c-8e60-0b5f3f7f72d0"
                    val expectedUserData = UserServer("e7e1d932-45c1-4e8c-8e60-0b5f3f7f72d0", "123Loyle-Carner321", "loyle@msn.mail")
                    val getUserByUUID = when (val result = inMemoryUserRepository.getUserById(uuidToRetrieve)) {
                        is UserRepositoryResult.SuccessReturnUserById -> result.userById
                        else -> {null}
                    }
                    getUserByUUID.shouldBe(expectedUserData)
                }
            }
            When("User does not exist in database") {
                Then("Repository should return null") {
                    val uuidNonExistent = "blabla-210911-bfkhwdbckaas-121djdbefj"
                    val getUserByUUID = inMemoryUserRepository.getUserById(uuidNonExistent)
                    getUserByUUID.shouldBe(UserRepositoryResult.UserNotFoundError)
                }
            }
        }

        When("User service attempts to create a new user") {
            When("New user does not already exist") {
                Then("Repository should create new user and return success response") {
                    val newUser = UserClient("ImNewHere555", "hello@new.here")
                    val createResponse = inMemoryUserRepository.createUser(newUser)
                    createResponse.shouldBe(UserRepositoryResult.SuccessNoContent)
                }
            }
            // duplicates will be handled at service layer - need to move these tests into UserServiceImplTest class
//            When("User with same email already exists") {
//                Then("Repository should not create new user and return error duplicate email response") {
//                    val newUserDuplicateEmail = UserClient("LolSameEmail_211", "lachie@lachiem1.com")
//                    val createResponse = inMemoryUserRepository.createUser(newUserDuplicateEmail)
//                    createResponse.shouldBe(UserRepositoryResult.DuplicateEmailError)
//                }
//            }
//            When("User with same username already exists") {
//                Then("Repository should not create new user and return error duplicate username response") {
//                    val newUserDuplicateUsername = UserClient("lachiem1", "same@username.com")
//                    val createResponse = inMemoryUserRepository.createUser(newUserDuplicateUsername)
//                    createResponse.shouldBe(UserRepositoryResult.DuplicateUsernameError)
//                }
//            }
        }

        When("User service attempts to update a user") {
            When("Update contains valid information") {
                Then("Repository should update existing user with new information and return success response") {
                    val updateExistingUserValidInfo = UserServer("91b7c88f-fc0a-41f7-a25d-5261c59b49f5", "NewUsername", "newEmail@rhcp.com")
                    val updateResponse = inMemoryUserRepository.updateUser(updateExistingUserValidInfo)
                    updateResponse.shouldBe(UserRepositoryResult.SuccessNoContent)
                }
            }
            // need to move these tests into UserServiceImplTest class
//            When("User does not exist in database") {
//                Then("Repository should not return user information and return error response indicating user not found") {
//                    val updateUserNotExisting = UserServer("uuid-that-does-not-exist", "NewUsername", "newEmail@user.com")
//                    val updateResponse = inMemoryUserRepository.updateUser(updateUserNotExisting)
//                    updateResponse.shouldBe(UserRepositoryResult.UserNotFoundError)
//                }
//            }
//            When("New username is already taken") {
//                Then("Repository should not return user information and return error response indicating username taken") {
//                    val duplicateUsername = "BlinkAndYoullMissit182"
//                    val updateUserUsernameTaken = UserServer("a0f5ae63-0b8a-4c79-9b89-2ee6d2c1c6da", duplicateUsername, "new@email.com")
//                    val updateResponse = inMemoryUserRepository.updateUser(updateUserUsernameTaken)
//                    updateResponse.shouldBe(UserRepositoryResult.DuplicateUsernameError)
//                }
//            }
//            When("New email is already taken") {
//                Then("Repository should not return user information and return error response indicating username taken") {
//                    val duplicateEmail = "loyle@msn.mail"
//                    val updateUserUsernameTaken = UserServer("a0f5ae63-0b8a-4c79-9b89-2ee6d2c1c6da", "newUsername123", duplicateEmail)
//                    val updateResponse = inMemoryUserRepository.updateUser(updateUserUsernameTaken)
//                    updateResponse.shouldBe(UserRepositoryResult.DuplicateEmailError)
//                }
//            }
        }

        When("User service attempts to delete a user") {
            When("User with matching id exists in database") {
                Then("Repository should delete user information from database and return a success response") {
                    val uuidToDelete = "91b7c88f-fc0a-41f7-a25d-5261c59b49f5"
                    val deleteResponse = inMemoryUserRepository.deleteUser(uuidToDelete)
                    deleteResponse.shouldBe(UserRepositoryResult.SuccessNoContent)
                }
            }
            // need to move these tests into UserServiceImplTest class
//            When("User with id does not exist in database") {
//                Then("Repository should not delete any users and return error response indicating user not found") {
//                    val uuidOfNonExistentUser = "this-uuid-does-not-correspond-to-any-users"
//                    val deleteResponse = inMemoryUserRepository.deleteUser(uuidOfNonExistentUser)
//                    deleteResponse.shouldBe(UserRepositoryResult.UserNotFoundError)
//                }
//            }
        }
    }
})

