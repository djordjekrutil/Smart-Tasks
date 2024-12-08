package com.djordjekrutil.tcp.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Float.fractionToDp(): Dp {
    LocalDensity.current
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    return (this@fractionToDp * screenHeightDp).dp
}
