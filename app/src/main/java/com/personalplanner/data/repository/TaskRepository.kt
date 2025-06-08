package com.personalplanner.data.repository

import com.personalplanner.data.database.TaskDao
import com.personalplanner.data.database.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // To be provided by Hilt
class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    fun getTasksForDate(date: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksForDate(date)
    }

    fun getTasksForWeek(startDate: String, endDate: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksForWeek(startDate, endDate)
    }

    suspend fun insertTask(task: TaskEntity): Long {
        // Potentially add logic here, e.g., setting orderIndex based on existing tasks
        val maxOrderIndex = taskDao.getMaxOrderIndexForDate(task.date) ?: -1
        val taskToInsert = if (task.orderIndex == 0 && task.id == 0L) { // Only auto-increment orderIndex for new tasks without a specific order
            task.copy(orderIndex = maxOrderIndex + 1)
        } else {
            task
        }
        return taskDao.insertTask(taskToInsert)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    suspend fun toggleTaskCompletion(taskId: Long) {
        taskDao.toggleTaskCompletion(taskId)
    }

    fun getTaskById(taskId: Long): Flow<TaskEntity?> {
        return taskDao.getTaskById(taskId)
    }

    // This function might be useful for reordering tasks within a specific date
    suspend fun updateTaskOrder(tasks: List<TaskEntity>) {
        // Room transactions are recommended for multiple DB operations
        // For simplicity, updating one by one. For complex reordering, a transaction is better.
        tasks.forEach { task ->
            taskDao.updateTask(task)
        }
    }
}
