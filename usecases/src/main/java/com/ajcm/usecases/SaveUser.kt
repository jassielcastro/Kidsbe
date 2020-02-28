package com.ajcm.usecases

import com.ajcm.data.repository.UserRepository
import com.ajcm.domain.User
import kotlinx.coroutines.InternalCoroutinesApi

class SaveUser(private val userRepository: UserRepository) {

    @InternalCoroutinesApi
    suspend fun invoke(user: User): String = userRepository.save(user)

}