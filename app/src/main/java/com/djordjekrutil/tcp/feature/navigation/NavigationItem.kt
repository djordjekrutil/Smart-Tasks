package com.djordjekrutil.tcp.feature.navigation

sealed class NavigationItem(var route: String) {
    data object Splash : NavigationItem("splash")
    data object Tasks : NavigationItem("tasks")
    data class Task(val taskId: String) : NavigationItem("tasks/$taskId") {
        companion object {
            const val route = "tasks/{id}"
        }
    }}