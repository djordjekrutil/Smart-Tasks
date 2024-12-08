package com.djordjekrutil.tcp.feature.view

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.djordjekrutil.tcp.ui.theme.TcpTheme
import com.djordjekrutil.tcp.core.platform.BaseActivity
import com.djordjekrutil.tcp.feature.navigation.Navigation
import com.djordjekrutil.tcp.feature.navigation.NavigationItem
import com.djordjekrutil.tcp.ui.theme.primary
import com.djordjekrutil.tcp.ui.utils.LoadingView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity @Inject constructor() : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            TcpTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = primary
                ) {
                    MainScreen(navController = navController, finishActivity = { finish() })
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    finishActivity: () -> Unit
) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    val previousRoute = remember { mutableStateOf<String?>(null) }
    val showLoading = remember { mutableStateOf(false) }

    LaunchedEffect(currentRoute) {
        if (previousRoute.value == NavigationItem.Splash.route && currentRoute == NavigationItem.Tasks.route) {
            showLoading.value = true
            delay(500)
            showLoading.value = false
        }
        previousRoute.value = currentRoute
    }

    if (showLoading.value) {
        LoadingView()
    } else {
        Navigation(navController = navController)
    }

    val isAtRootDestination = currentRoute == NavigationItem.Tasks.route
    BackHandler(enabled = isAtRootDestination) {
        finishActivity()
    }
}
