package com.learning.leap.bwb.home

import android.net.Uri
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
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
fun HomeScreen(navigator: DestinationsNavigator){
    val webSiteLink  = "http://leapempowers.org/"
    val uriHandler = LocalUriHandler.current
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
            )
            
            Row( modifier = Modifier.padding(top = 14.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.home_playtoday),
                    contentDescription = " Background",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.padding(end = 100.dp)
                        .clickable {
                            // open play today
                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.home_library),
                    contentDescription = " Background",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.clickable {
                        // open library
                    }
                )
            }

            Image(
                painter = painterResource(id = R.drawable.home_settings),
                contentDescription = " Background",
                contentScale = ContentScale.Fit,
                modifier = Modifier.padding(top = 20.dp)
                    .clickable {
                        // open settings
                    }
            )

            Row(modifier = Modifier.padding(top = 20.dp).clickable {
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