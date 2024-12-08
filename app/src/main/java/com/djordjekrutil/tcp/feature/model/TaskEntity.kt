package com.djordjekrutil.tcp.feature.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val targetDate: String,
    val dueDate: String?,
    val title: String,
    val description: String,
    val priority: Int,
    val isResolved: Boolean? = null,
    val comment : String = ""
)