package com.ajcm.usecases

import com.ajcm.data.repository.UserRepository
import com.ajcm.domain.User

class GetUserProfile(private val userRepository: UserRepository) {

    suspend fun invoke(userId: String): User? = userRepository.search(userId)

}