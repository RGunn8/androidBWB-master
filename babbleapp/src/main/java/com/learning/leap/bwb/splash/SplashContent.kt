package com.learning.leap.bwb.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learning.leap.bwb.destinations.DownloadScreenDestination
import com.learning.leap.bwb.destinations.HomeScreenDestination
import com.learning.leap.bwb.destinations.UserSettingsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination(start = true)
fun SplashContent(
    viewModel: SplashViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val destination: SplashScreenDestination by viewModel.splashDestination.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

    }
    when (destination) {
        SplashScreenDestination.HOME -> navigator.navigate(HomeScreenDestination())
        SplashScreenDestination.USER_SETTINGS -> navigator.navigate(UserSettingsScreenDestination(newUser = true))
        SplashScreenDestination.DOWNLOAD -> navigator.navigate(DownloadScreenDestination())
        SplashScreenDestination.NONE -> {}
    }
}