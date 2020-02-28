package com.ajcm.usecases

import com.ajcm.data.repository.UserRepository
import com.ajcm.domain.User
import kotlinx.coroutines.InternalCoroutinesApi

class UpdateUser(private val userRepository: UserRepository) {

    @InternalCoroutinesApi
    suspend fun invoke(user: User): Boolean = userRepository.update(user)

}