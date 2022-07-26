package com.taptri.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.taptri.model.UrlEntity

@Database(entities = [UrlEntity::class], version = 1)
abstract class UrlDataBase : RoomDatabase() {
    abstract fun getDao(): UrlDao

    companion object {
        @Volatile
        private var instance: UrlDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext, UrlDataBase::class.java, "urldatabase"
        ).build()


    }

}