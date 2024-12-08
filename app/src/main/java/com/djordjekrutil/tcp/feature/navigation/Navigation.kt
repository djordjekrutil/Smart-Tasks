package com.djordjekrutil.tcp.feature.navigation

import TasksScreen
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.djordjekrutil.tcp.feature.view.SplashScreen
import com.djordjekrutil.tcp.feature.view.TaskDetailsScreen
import com.djordjekrutil.tcp.feature.viewmodel.TaskDetailsViewModel
import com.djordjekrutil.tcp.feature.viewmodel.TasksViewModel

@Composable
fun Navigation(
    navController: NavHostController
) {
    NavHost(navController, startDestination = NavigationItem.Splash.route) {
        composable(NavigationItem.Tasks.route) {
            val tasksViewModel: TasksViewModel = hiltViewModel()
            TasksScreen(tasksViewModel, navController)
        }

        composable(NavigationItem.Splash.route) {
            SplashScreen {
                navController.navigate(NavigationItem.Tasks.route) {
                    popUpTo(NavigationItem.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable(
            NavigationItem.Task.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            val taskDetailsViewModel: TaskDetailsViewModel = hiltViewModel()
            TaskDetailsScreen(taskDetailsViewModel, navController)
        }
    }
}