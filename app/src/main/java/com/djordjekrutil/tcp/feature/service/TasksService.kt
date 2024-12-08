package com.djordjekrutil.tcp.feature.service

import com.djordjekrutil.tcp.feature.model.TasksResponse
import com.djordjekrutil.tcp.feature.service.api.TasksApi
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksService
@Inject constructor(retrofit: Retrofit) : TasksApi {

    private val tasksApi by lazy { retrofit.create(TasksApi::class.java) }

    override fun getTasks(): Call<TasksResponse> = tasksApi.getTasks()
}