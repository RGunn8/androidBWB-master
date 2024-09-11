@file:OptIn(ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class)

package com.learning.leap.bwb.home

import android.icu.util.Calendar
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.learning.leap.bwb.BuildConfig
import com.learning.leap.bwb.R
import com.learning.leap.bwb.destinations.LibraryCategoryScreenDestination
import com.learning.leap.bwb.destinations.PlayTodayScreenDestination
import com.learning.leap.bwb.destinations.SettingScreenDestination
import com.learning.leap.bwb.localNotification.TipNotification
import com.learning.leap.bwb.localNotification.TipScheduler
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@Composable
@Destination
fun HomeScreen(navigator: DestinationsNavigator){
    val webSiteLink  = "http://leapempowers.org/"
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPremission =
            rememberPermissionState(
                permission =
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        if (!notificationPremission.status.isGranted) {
            LaunchedEffect(key1 = Unit) {
                notificationPremission.launchPermissionRequest()
            }
        }
    }
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.splash_home_bg),
            contentDescription = " Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_bwbicon),
                contentDescription = " Background",
                contentScale = ContentScale.Fit,
                modifier = Modifier.clickable {
                    if (BuildConfig.DEBUG) {
                        val oneMinFromNow = Calendar.getInstance().apply {

                        }.timeInMillis
                        val tipScheduler = TipScheduler(context)
                        tipScheduler.testSchedule(TipNotification(oneMinFromNow, 2, 3))
                    }
                }
            )
            
            Row( modifier = Modifier.padding(top = 14.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.home_playtoday),
                    contentDescription = " Background",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(end = 100.dp)
                        .clickable {
                            navigator.navigate(PlayTodayScreenDestination)
                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.home_library),
                    contentDescription = " Background",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.clickable {
                        navigator.navigate(LibraryCategoryScreenDestination(isSubCategory = false))
                    }
                )
            }

            Image(
                painter = painterResource(id = R.drawable.home_settings),
                contentDescription = " Background",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable {
                        navigator.navigate(SettingScreenDestination)
                    }
            )

            Row(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable {
                        uriHandler.openUri(uri = webSiteLink)
                    },
                verticalAlignment = Alignment.CenterVertically){
                Text(stringResource(id = R.string.powered_by),
                    fontSize = 20.sp, modifier = Modifier.padding(end = 4.dp))
                Image(painter = painterResource(id = R.drawable.leaplogo), contentDescription = "Leap Logo" )
            }
            
        }
    }
}

@Composable
@Preview
fun HomeScreenPreview(){
    HomeScreen(navigator = EmptyDestinationsNavigator)
}