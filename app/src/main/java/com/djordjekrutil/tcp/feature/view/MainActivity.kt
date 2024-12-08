package com.djordjekrutil.tcp.feature.view

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.djordjekrutil.tcp.ui.theme.TcpTheme
import com.djordjekrutil.tcp.core.platform.BaseActivity
import com.djordjekrutil.tcp.feature.navigation.Navigation
import com.djordjekrutil.tcp.feature.navigation.NavigationItem
import com.djordjekrutil.tcp.ui.theme.primary
import dagger.hilt.android.AndroidEntryPoint
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
    val isAtRootDestination =
        currentBackStackEntry.value?.destination?.route == NavigationItem.Tasks.route

    BackHandler(enabled = isAtRootDestination) {
        finishActivity()
    }

    Navigation(navController = navController)
}
