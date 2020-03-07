package com.ajcm.usecases

import com.ajcm.data.repository.UserRepository
import com.ajcm.domain.User

class SaveUser(private val userRepository: UserRepository) {

    suspend fun invoke(user: User): String = userRepository.save(user)

}