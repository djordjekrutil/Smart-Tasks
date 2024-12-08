package com.djordjekrutil.tcp.feature.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.djordjekrutil.tcp.feature.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTasks(tasks: List<TaskEntity>)

    @Query("SELECT * FROM tasks")
    fun getTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTask(taskId: String): Flow<TaskEntity>

    @Query("UPDATE tasks SET isResolved = :resolved WHERE id = :taskId")
    fun resolveTask(taskId: String, resolved: Boolean)

    @Query("UPDATE tasks SET comment = :comment WHERE id = :taskId")
    fun commentTask(taskId: String, comment: String)

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun count(): Int
}