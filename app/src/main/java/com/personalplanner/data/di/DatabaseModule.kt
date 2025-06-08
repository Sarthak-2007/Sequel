package com.personalplanner.data.di

import android.content.Context
import androidx.room.Room
import com.personalplanner.data.database.TaskDao
import com.personalplanner.data.database.TaskDatabase
import com.personalplanner.utils.DateUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Lives as long as the application
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTaskDatabase(@ApplicationContext context: Context): TaskDatabase {
        return TaskDatabase.getDatabase(context)
    }

    @Provides
    @Singleton // TaskDao is tied to the singleton TaskDatabase instance
    fun provideTaskDao(database: TaskDatabase): TaskDao {
        return database.taskDao()
    }
}
