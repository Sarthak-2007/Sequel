package com.personalplanner.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY orderIndex ASC, createdAt ASC")
    fun getTasksForDate(date: String): Flow<List<TaskEntity>>

    // Assuming startDate and endDate are inclusive
    @Query("SELECT * FROM tasks WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, orderIndex ASC, createdAt ASC")
    fun getTasksForWeek(startDate: String, endDate: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = NOT isCompleted WHERE id = :taskId")
    suspend fun toggleTaskCompletion(taskId: Long)

    // Added for reordering, if needed later
    @Query("SELECT MAX(orderIndex) FROM tasks WHERE date = :date")
    suspend fun getMaxOrderIndexForDate(date: String): Int?

    // Added to get a single task, potentially useful for updates or specific operations
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<TaskEntity?>
}
