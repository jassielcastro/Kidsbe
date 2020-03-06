package com.ajcm.data.database

import androidx.room.*
import com.ajcm.data.models.UserDb

@Dao
interface UserDAO {

    @Query("SELECT * FROM UserDb")
    fun getAll(): List<UserDb>

    @Query("SELECT * FROM UserDb LIMIT 1")
    fun findLast(): UserDb

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserDb)

    @Update
    fun update(user: UserDb)

    @Delete
    fun delete(user: UserDb)

}

