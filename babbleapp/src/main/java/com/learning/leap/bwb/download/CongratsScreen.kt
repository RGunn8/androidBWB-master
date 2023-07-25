package com.learning.leap.bwb.download

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learning.leap.bwb.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@Composable
@Destination
fun CongratsScreen(navigator:DestinationsNavigator){
    Box(Modifier.fillMaxSize()) {
          Image(painter = painterResource(id = R.drawable.splash_home_bg), contentDescription = "Congrat Background",
                       contentScale = ContentScale.FillBounds, modifier = Modifier.fillMaxSize())
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.congrats),
                contentDescription = "Congrat Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.padding(top = 30.dp)
            )
            Text(
                text = stringResource(id = R.string.congrats_first_message),
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 50.dp, start = 20.dp, end = 20.dp)
            )
            Text(
                text = stringResource(id = R.string.congrats_second_message),
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 30.dp, start = 20.dp, end = 20.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.congrat_continue),
                contentDescription = "Congrats Continue Button",
                modifier = Modifier.padding(top = 30.dp).clickable {

                }
            )

        }
    }
}

@Composable
@Preview
fun CongratsPreview(){
    CongratsScreen(EmptyDestinationsNavigator)
}