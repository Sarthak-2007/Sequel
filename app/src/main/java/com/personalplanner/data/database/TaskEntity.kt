package com.personalplanner.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: String, // Should be LocalDate.toString()
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val orderIndex: Int = 0 // Renamed from order_index to follow Kotlin conventions
)
