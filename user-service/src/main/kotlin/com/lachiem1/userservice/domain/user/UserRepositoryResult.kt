package com.lachiem1.userservice.domain.user

sealed class UserRepositoryResult {
    object DuplicateEmailError : UserRepositoryResult()
    object DuplicateUsernameError : UserRepositoryResult()
    object UserNotFoundError : UserRepositoryResult()
    data class UnexpectedException(val exception: Throwable?) : UserRepositoryResult()
    object SuccessNoContent : UserRepositoryResult()
    data class SuccessReturnAllUsers(val allUsers: List<UserServer>) : UserRepositoryResult()
    data class SuccessReturnUserById(val userById: UserServer) : UserRepositoryResult()
}