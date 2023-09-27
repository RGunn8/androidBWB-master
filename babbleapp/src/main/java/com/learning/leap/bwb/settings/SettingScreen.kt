package com.learning.leap.bwb.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learning.leap.bwb.BackButton
import com.learning.leap.bwb.R
import com.learning.leap.bwb.destinations.UserSettingsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@Composable
@Destination
fun SettingScreen(navigator: DestinationsNavigator) {
    Column(modifier = Modifier.fillMaxSize()) {
        BackButton {
            navigator.popBackStack()
        }
        SettingContent(navigator)
    }
}

@Composable
private fun SettingContent(navigator: DestinationsNavigator) {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.setting_bg),
            contentDescription = "Setting Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val settingDetailArray = stringArrayResource(id = R.array.setting_detail)
            val settingTitleArray = stringArrayResource(id = R.array.setting_titles)
            val settingPair = settingDetailArray.mapIndexed { index, s ->
                return@mapIndexed Pair(settingTitleArray[index], s)
            }

            Image(
                painter = painterResource(id = R.drawable.settings_icon),
                contentDescription = "Setting Icon",
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 24.dp)
            )
            settingPair.forEachIndexed { index, pairs ->
                Column(modifier = Modifier
                    .clickable {
                        when (index) {
                            0 -> {
                                navigator.navigate(UserSettingsScreenDestination(false))
                            }

                            1 -> {}
                            else -> {

                            }
                        }
                    }
                    .background(Color.White)
                    .fillMaxWidth(), verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = pairs.first,
                        fontSize = 30.sp,
                        color = colorResource(id = R.color.dark_green),
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        text = pairs.second,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp)
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
@Preview
fun SettingScreenPreviews(){
    SettingScreen(EmptyDestinationsNavigator)
}