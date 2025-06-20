package com.personalplanner.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database" // Name of the database file
                )
                // Potentially add migrations here if schema changes in the future
                // .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration() // For simplicity during development, replace with proper migrations for production
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
