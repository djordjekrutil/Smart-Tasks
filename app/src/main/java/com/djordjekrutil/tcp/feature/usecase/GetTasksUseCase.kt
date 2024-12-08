package com.djordjekrutil.tcp.feature.usecase

import com.djordjekrutil.tcp.core.exception.Failure
import com.djordjekrutil.tcp.core.functional.Either
import com.djordjekrutil.tcp.core.interactor.UseCase
import com.djordjekrutil.tcp.feature.model.TaskEntity
import com.djordjekrutil.tcp.feature.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val tasksRepository: TasksRepository
) : UseCase<Flow<List<TaskEntity>>, UseCase.None>() {

    override suspend fun run(params: None): Either<Failure, Flow<List<TaskEntity>>> {
        return tasksRepository.getTasks()
    }
}