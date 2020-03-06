package com.ajcm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ajcm.data.models.UserDb

@Database(entities = [UserDb::class], version = 2)
abstract class KidstubeDB : RoomDatabase() {

    companion object {
        fun build(context: Context) = Room.databaseBuilder(
            context,
            KidstubeDB::class.java,
            "kidstube-db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    abstract fun userDao(): UserDAO
}