package com.ajcm.data.source

import com.ajcm.data.database.KidstubeDB
import com.ajcm.data.mappers.toRoomUser
import com.ajcm.data.mappers.toUser
import com.ajcm.domain.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomDataSource(db: KidstubeDB) : LocalDataSource<User> {

    private val userDao = db.userDao()

    override suspend fun exist(): Boolean = withContext(Dispatchers.IO) {
        userDao.getAll().isNotEmpty()
    }

    override suspend fun save(obj: User) = withContext(Dispatchers.IO) {
        userDao.insert(obj.toRoomUser())
    }

    override suspend fun getObject(): User = withContext(Dispatchers.IO) {
        userDao.findLast().toUser()
    }

    override suspend fun update(obj: User) = withContext(Dispatchers.IO) {
        userDao.update(obj.toRoomUser())
    }

    override suspend fun delete(obj: User) = withContext(Dispatchers.IO) {
        userDao.delete(obj.toRoomUser())
    }
}