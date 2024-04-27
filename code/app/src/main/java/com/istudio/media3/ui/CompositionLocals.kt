package com.istudio.media3.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavigationProvider = staticCompositionLocalOf<NavHostController> { error("No navigation host controller provided.") }