package com.learning.leap.bwb.splash

import android.text.Layout
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.leap.bwb.R
import com.learning.leap.bwb.destinations.UserSettingsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination(start = true)
fun SplashContent(
    viewModel: SplashViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val destination: SplashDestination by viewModel.destinationLiveData.observeAsState(
        SplashDestination.UserSetting
    )

    LaunchedEffect(key1 = Unit, block = {
        viewModel.displayTimer()
    })
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_logo_3x),
            contentDescription = "splash logo"
        )
    }



    when (destination) {
        SplashDestination.Download -> Log.d("test", "download")
        SplashDestination.Home -> Log.d("test", "Home")
        SplashDestination.UserSetting -> navigator.navigate(UserSettingsScreenDestination(true), builder = {
            this.launchSingleTop = true
        })
    }
}