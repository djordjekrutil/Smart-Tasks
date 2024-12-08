package com.djordjekrutil.tcp.feature.repository

import com.djordjekrutil.tcp.core.exception.Failure
import com.djordjekrutil.tcp.core.functional.Either
import com.djordjekrutil.tcp.core.interactor.UseCase
import com.djordjekrutil.tcp.core.platform.NetworkHandler
import com.djordjekrutil.tcp.feature.db.AppDatabase
import com.djordjekrutil.tcp.feature.model.Task
import com.djordjekrutil.tcp.feature.model.TaskEntity
import com.djordjekrutil.tcp.feature.service.TasksService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface TasksRepository {

    suspend fun syncTasks(): Either<Failure, UseCase.None>
    suspend fun getTasks(): Either<Failure, Flow<List<TaskEntity>>>
    suspend fun getTask(taskId: String): Either<Failure, Flow<TaskEntity>>
    suspend fun resolveTask(taskId: String, isResolved: Boolean): Either<Failure, UseCase.None>
    suspend fun commentTask(
        taskId: String,
        comment: String
    ): Either<Failure, UseCase.None>

    class Network @Inject constructor(
        private val networkHandler: NetworkHandler,
        private val tasksService: TasksService
    ) {
        fun fetchTasks(): Either<Failure, List<Task>> {
            return if (networkHandler.isConnected) {
                val response = tasksService.getTasks().execute()
                response.body()?.let {
                    it.tasks.let { tasks ->
                        Either.Right(tasks)
                    }
                } ?: Either.Left(Failure.ServerError)
            } else {
                Either.Left(Failure.NetworkConnection)
            }
        }
    }

    class Database @Inject constructor(
        private val appDatabase: AppDatabase
    ) {
        fun getTasks() = appDatabase.TasksDao().getTasks()

        fun getTask(taskId: String) = appDatabase.TasksDao().getTask(taskId)

        fun insertTasks(tasks: List<TaskEntity>): Either<Failure, UseCase.None> {
            appDatabase.TasksDao().insertTasks(tasks)
            return Either.Right(UseCase.None())
        }

        fun resolveTask(taskId: String, isResolved: Boolean): Either<Failure, UseCase.None> {
            appDatabase.TasksDao().resolveTask(taskId, isResolved)
            return Either.Right(UseCase.None())
        }

        fun commentTask(
            taskId: String,
            comment: String
        ): Either<Failure, UseCase.None> {
            appDatabase.TasksDao().commentTask(taskId, comment)
            return Either.Right(UseCase.None())
        }
    }

    class TasksRepositoryImpl @Inject constructor(
        private val network: Network,
        private val database: Database
    ) : TasksRepository {

        override suspend fun syncTasks(): Either<Failure, UseCase.None> {
            return when (val networkResult = network.fetchTasks()) {
                is Either.Right -> {
                    val taskEntities: List<TaskEntity> =
                        networkResult.b.map { task -> task.toEntity() }
                    database.insertTasks(taskEntities)
                    Either.Right(UseCase.None())
                }

                is Either.Left -> {
                    Either.Left(networkResult.a) // Propagate the error
                }
            }
        }

        override suspend fun getTasks(): Either<Failure, Flow<List<TaskEntity>>> {
            return try {
                val tasksDBFlow = database.getTasks()
                val tasksDB = withContext(Dispatchers.IO) {
                    tasksDBFlow.first()
                }
                if (tasksDB.isNotEmpty()) {
                    Either.Right(tasksDBFlow)
                } else {
                    when (val networkResult = network.fetchTasks()) {
                        is Either.Right -> {
                            val tasks = networkResult.b.map { it.toEntity() }
                            database.insertTasks(tasks)
                            Either.Right(database.getTasks())
                        }

                        is Either.Left -> {
                            Either.Left(networkResult.a)
                        }
                    }
                }
            } catch (e: Exception) {
                Either.Left(Failure.DatabaseError)
            }
        }

        override suspend fun getTask(taskId: String): Either<Failure, Flow<TaskEntity>> {
            return database.getTask(taskId).let {
                Either.Right(it)
            }
        }

        override suspend fun resolveTask(
            taskId: String,
            isResolved: Boolean
        ): Either<Failure, UseCase.None> {
            return database.resolveTask(taskId, isResolved)
        }

        override suspend fun commentTask(
            taskId: String,
            comment: String
        ): Either<Failure, UseCase.None> {
            return database.commentTask(taskId, comment)
        }
    }
}
