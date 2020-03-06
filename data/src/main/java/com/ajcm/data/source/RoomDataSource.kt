package com.ajcm.data.source

import com.ajcm.data.database.KidstubeDB
import com.ajcm.data.mappers.toRoomUser
import com.ajcm.data.mappers.toUser
import com.ajcm.domain.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomDataSource(db: KidstubeDB) : LocalDataSource {

    private val userDao = db.userDao()

    override suspend fun existUser(): Boolean = withContext(Dispatchers.IO) {
        userDao.getAll().isNotEmpty()
    }

    override suspend fun saveUser(user: User) = withContext(Dispatchers.IO) {
        userDao.insert(user.toRoomUser())
    }

    override suspend fun getUser(): User = withContext(Dispatchers.IO) {
        userDao.findLast().toUser()
    }

    override suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        userDao.update(user.toRoomUser())
    }

    override suspend fun deleteUser(user: User) = withContext(Dispatchers.IO) {
        userDao.delete(user.toRoomUser())
    }
}