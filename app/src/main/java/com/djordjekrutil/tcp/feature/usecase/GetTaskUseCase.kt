package com.djordjekrutil.tcp.feature.usecase

import com.djordjekrutil.tcp.core.exception.Failure
import com.djordjekrutil.tcp.core.functional.Either
import com.djordjekrutil.tcp.core.interactor.UseCase
import com.djordjekrutil.tcp.feature.model.TaskEntity
import com.djordjekrutil.tcp.feature.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaskUseCase @Inject constructor(
    private val tasksRepository: TasksRepository
) : UseCase<Flow<TaskEntity>, String>() {

    override suspend fun run(params: String): Either<Failure, Flow<TaskEntity>> {
        return tasksRepository.getTask(params)
    }
}