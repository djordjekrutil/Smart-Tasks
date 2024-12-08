package com.djordjekrutil.tcp.feature.model

import com.google.gson.annotations.SerializedName

data class Task(
    val id: String,
    @SerializedName("TargetDate") val targetDate: String,
    @SerializedName("DueDate") val dueDate: String?,
    @SerializedName("Title") val title: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Priority") val priority: Int
) {
    fun toEntity(): TaskEntity {
        return TaskEntity(
            id = this.id,
            targetDate = this.targetDate,
            dueDate = this.dueDate,
            title = this.title,
            description = this.description,
            priority = this.priority
        )
    }
}