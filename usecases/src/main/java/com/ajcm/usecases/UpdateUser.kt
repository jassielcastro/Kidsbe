package com.ajcm.usecases

import com.ajcm.data.repository.UserRepository
import com.ajcm.domain.User

class UpdateUser(private val userRepository: UserRepository) {

    suspend fun invoke(user: User): Boolean = userRepository.update(user)

}