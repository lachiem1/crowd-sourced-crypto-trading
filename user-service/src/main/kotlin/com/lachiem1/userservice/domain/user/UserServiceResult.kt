package com.lachiem1.userservice.domain.user

sealed class UserServiceResult {
    object EmailAlreadyTakenError : UserServiceResult()
    object UsernameAlreadyTakenError : UserServiceResult()
    object NotFoundError : UserServiceResult()
    data class ValidationError(val field: String, val reason: String?) : UserServiceResult()
    data class DownstreamSystemError(val exception: Throwable?) : UserServiceResult()
    object SuccessNoContent : UserServiceResult()
    data class SuccessReturnAllUsers(val allUsers: List<UserServer>) : UserServiceResult()
    data class SuccessReturnUserById(val userById: UserServer) : UserServiceResult()
}