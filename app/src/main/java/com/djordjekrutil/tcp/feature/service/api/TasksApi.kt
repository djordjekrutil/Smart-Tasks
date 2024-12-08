package com.djordjekrutil.tcp.feature.service.api

import retrofit2.Call
import retrofit2.http.GET
import com.djordjekrutil.tcp.feature.model.TasksResponse

interface TasksApi {

    companion object {
        private const val TASKS = "."
    }

    @GET(TASKS)
    fun getTasks(): Call<TasksResponse>
}
