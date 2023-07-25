package com.learning.leap.bwb.download

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learning.leap.bwb.R
import com.learning.leap.bwb.theme.BabbleTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun DownloadScreen(viewModel: DownloadViewModel = hiltViewModel(), navigator: DestinationsNavigator){
    val state:DownloadScreenState by viewModel.downloadState.collectAsStateWithLifecycle()

BabbleTheme {
    when(val screenState = state){
        is DownloadScreenState.Downloading -> {
            DownloadContent(progress = screenState.progress)
        }
        DownloadScreenState.Error -> {
            DownloadContent(displayError = true) {
                viewModel.retryDownload()
            }
        }
        DownloadScreenState.Starting -> {
            DownloadContent()
        }
        DownloadScreenState.Success -> {
            // navigator to success
            Log.d("Test","download was successful")
        }
    }
}
}

@Composable
fun DownloadContent(progress:Int = 0,displayError:Boolean = false, onError:() -> Unit = {}){
    val displayAlertDialog = remember{ mutableStateOf(false) }

    Box(Modifier.fillMaxSize()){
        Image(painter = painterResource(id = R.drawable.splash_home_bg), contentDescription = "Image background",
            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                displayAlertDialog.value = displayError
                if (displayAlertDialog.value){
                    AlertDialog(onDismissRequest = {displayAlertDialog.value = false},
                        title = {""}, buttons = {

                    })
                }
                if (progress <= 50) {
                    DownloadMessageText(stringId = R.string.download_in_progress)
                } else if (progress in 51..74) {
                    DownloadMessageText(stringId = R.string.almostDone)
                } else {
                    DownloadMessageText(stringId = R.string.wrapping_up)
                }
                Text(
                    text = stringResource(id = R.string.download_files_info),
                    modifier = Modifier.padding(16.dp)
                )
                LinearProgressIndicator(progress = progress.toFloat() / 100)

                Text(text = "${progress}%",  modifier = Modifier.padding(16.dp), fontSize = 18.sp)
            }
    }
}

@Composable
private fun DownloadMessageText(stringId:Int){
    Text(text = stringResource(id = stringId).uppercase(), fontSize = 30.sp, color = Color.Black )
}

@Preview
@Composable
fun DownloadPreview(){
    DownloadContent(progress = 70, displayError = false )
}