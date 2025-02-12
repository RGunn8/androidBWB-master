package com.learning.leap.bwb.download

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.learning.leap.bwb.R
import com.learning.leap.bwb.destinations.CongratsScreenDestination
import com.learning.leap.bwb.theme.BabbleTheme
import com.learning.leap.bwb.worker.ResetPlayTodayWorker
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Composable
@Destination
fun DownloadScreen(
    viewModel: DownloadViewModel = hiltViewModel(),
    fromAgeRange: Boolean = false,
    navigator: DestinationsNavigator
) {
    val state:DownloadScreenState by viewModel.downloadState.collectAsStateWithLifecycle()

    val context = LocalContext.current

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
            if (fromAgeRange) {
                navigator.popBackStack()
            } else {
                viewModel.scheduleTips()
                navigator.navigate(CongratsScreenDestination)
                setResetPlayTodayWorker(context)
            }
        }

    }
}
}


fun setResetPlayTodayWorker(context: Context) {
    val now = Calendar.getInstance()
    val next4AM = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 4)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (before(now)) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }
    val initialDelayMillis = next4AM.timeInMillis - now.timeInMillis

    val periodicWorkRequest = PeriodicWorkRequestBuilder<ResetPlayTodayWorker>(
        24, TimeUnit.HOURS // Repeat interval: every 24 hours
    )
        .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueue(periodicWorkRequest)
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
                        title = { stringResource(id = R.string.BabbleError)}, text = { Text(text = stringResource(
                            id = R.string.downloadError
                        ))}, confirmButton = {
                            Button(onClick = {onError.invoke()}) {
                                Text(text = "Retry")
                            }
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
                    modifier = Modifier.padding(16.dp),
                    fontSize = 20.sp
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