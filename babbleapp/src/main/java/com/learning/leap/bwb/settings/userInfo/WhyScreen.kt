package com.learning.leap.bwb.settings.userInfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learning.leap.bwb.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun WhyScreen(navigator: DestinationsNavigator) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            stringResource(R.string.why),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            stringResource(R.string.babyBDay), modifier = Modifier.padding(bottom = 10.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            stringResource(R.string.babyBdayInfo),
            modifier = Modifier.padding(bottom = 10.dp),
            fontSize = 16.sp
        )
        Text(
            stringResource(R.string.babyFirstName), modifier = Modifier.padding(bottom = 10.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            stringResource(R.string.babyFirstNameInfo),
            modifier = Modifier.padding(bottom = 10.dp),
            fontSize = 16.sp
        )
        Text(
            stringResource(R.string.baby_s_gender), modifier = Modifier.padding(bottom = 10.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            stringResource(R.string.baby_gender_info),
            modifier = Modifier.padding(bottom = 10.dp),
            fontSize = 16.sp
        )
        Image(
            painter = painterResource(id = R.drawable.whyneedinfo_backtosettings),
            contentDescription = "null",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .clickable {
                    navigator.popBackStack()
                }
        )
    }

}