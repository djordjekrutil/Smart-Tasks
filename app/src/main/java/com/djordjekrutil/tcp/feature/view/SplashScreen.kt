package com.djordjekrutil.tcp.feature.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.djordjekrutil.tcp.R
import com.djordjekrutil.tcp.feature.viewmodel.SplashState
import com.djordjekrutil.tcp.feature.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToNextScreen: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    when (state) {
        is SplashState.NavigateToNextScreen -> {
            onNavigateToNextScreen()
        }

        is SplashState.Error -> {
            val errorMessage = (state as SplashState.Error).message
            Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_SHORT).show()
        }

        else -> {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val availableHeight = maxHeight
                val logoHeightFraction = 0.3f
                val logoHeight = availableHeight * logoHeightFraction

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(logoHeight)
                    )

                    if (state is SplashState.ShowProgress) {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator()
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = R.drawable.intro_illustration),
                        contentDescription = "Illustration",
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}
