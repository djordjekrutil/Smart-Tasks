package com.djordjekrutil.tcp.feature.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.djordjekrutil.tcp.feature.db.dao.TasksDao
import com.djordjekrutil.tcp.feature.model.TaskEntity

@Database(
    entities = arrayOf(TaskEntity::class), version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun TasksDao(): TasksDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tcp-database"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}