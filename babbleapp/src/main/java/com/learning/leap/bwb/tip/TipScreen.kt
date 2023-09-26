package com.learning.leap.bwb.tip

import android.content.Context
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.learning.leap.bwb.R
import io.sanghun.compose.video.VideoPlayer
import io.sanghun.compose.video.uri.VideoPlayerMediaItem
import java.io.File

@Composable
fun TipScreen(
    isLoading:Boolean,
    displayMessage:String = "",
    displaySoundButton: Boolean = false,
    displayVideoButton : Boolean = false,
    videoFileName:String? = null,
    displayBackButton:Boolean = false,
    displayNext:Boolean = false,
    backgroundId:Int,
    playSoundButtonId: Int,
    pauseSoundButtonId: Int,
    screenIconId:Int,
    playVideoButtonId:Int,
    previousButtonID:Int,
    nextButtonID:Int,
    mediaPlayer: MediaPlayer? = null,
    isFavorite: Boolean = false,
    onVideoButtonClicked: (Boolean) -> Unit,
    onNextPressed: () -> Unit,
    onPrevPressed: () -> Unit,
    onBackPressed: () -> Unit,
    onFavoriteClicked: () -> Unit,
){
    val context = LocalContext.current
    var isPlayingAudio by remember {
        mutableStateOf(false)
    }
    var isPlayingVideo by remember {
        mutableStateOf(false)
    }
    BackHandler {
        if (isPlayingVideo){
            isPlayingVideo = false
        }else{
            onBackPressed.invoke()
        }
    }
    val soundImageId = if (isPlayingAudio) {pauseSoundButtonId} else{playSoundButtonId}
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundId), contentDescription = " Background",
            contentScale = ContentScale.FillBounds, modifier = Modifier.fillMaxSize()
        )
        if (isLoading) {
           LoadingScreen(
               soundImageId = soundImageId,
               screenIconId = screenIconId,
               playVideoButtonId = playVideoButtonId
           )
        } else {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isPlayingVideo) {
                     TipVideoPlayer(context, videoFileName)
                    onVideoButtonClicked.invoke(isPlayingVideo)
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 30.dp),
                    Arrangement.SpaceEvenly,
                ) {

                    Image(painter = painterResource(id = soundImageId),
                        contentDescription = "Play sound button",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(70.dp)
                            .alpha(
                                if (displaySoundButton) {
                                    1f
                                } else {
                                    0f
                                }
                            )
                            .clickable {
                                if (isPlayingAudio) {
                                    mediaPlayer?.stop()
                                    isPlayingAudio = false
                                } else {
                                    mediaPlayer?.prepareAsync()
                                    mediaPlayer?.setOnPreparedListener {
                                        it.start()
                                        isPlayingAudio = true
                                    }
                                }
                            })
                    Image(
                        painter = painterResource(id = screenIconId),
                        contentDescription = "Screen icon",
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.FillBounds
                    )
                    Image(painter = painterResource(id = playVideoButtonId),
                        contentDescription = "Play video button",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(70.dp)
                            .alpha(
                                if (displayVideoButton) {
                                    1f
                                } else {
                                    0f
                                }
                            )
                            .clickable {
                                isPlayingVideo = true
                            })
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = displayMessage,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 30.dp),
                    Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Image(painter = painterResource(id = previousButtonID),
                        contentDescription = "Play sound button",
                        modifier = Modifier
                            .alpha(
                                if (displayBackButton) {
                                    1f
                                } else {
                                    0f
                                }
                            )
                            .clickable {
                                onPrevPressed.invoke()
                            })
                    Image(
                        painter = painterResource(
                            id = if (isFavorite) R.drawable.heartfilled else {
                                R.drawable.heart
                            }
                        ), contentDescription = "Screen icon", modifier = Modifier.clickable {
                            onFavoriteClicked.invoke()
                        })
                    Image(painter = painterResource(id = nextButtonID),
                        contentDescription = "Play video button",
                        modifier = Modifier
                            .alpha(
                                if (displayNext) {
                                    1f
                                } else {
                                    0f
                                }
                            )
                            .clickable {
                                onNextPressed.invoke()
                            })
                }
            }
        }
    }
}

@Composable
private fun TipVideoPlayer(
    context: Context,
    videoFileName: String?,
) {
    videoFileName?.let {
        val file = File(context.filesDir, videoFileName)

        val videoFile = VideoPlayerMediaItem.StorageMediaItem(
            storageUri = file.toUri()
        )
        VideoPlayer(
            mediaItems = listOf(videoFile), modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Composable
fun LoadingScreen(soundImageId:Int,screenIconId: Int,playVideoButtonId: Int){
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 30.dp),
            Arrangement.SpaceEvenly,
        ) {

            Image(painter = painterResource(id = soundImageId),
                contentDescription = "Play sound button",
                modifier = Modifier
                    .size(70.dp)
                    .alpha(0f)
            )
            Image(
                painter = painterResource(id = screenIconId),
                contentDescription = "Screen icon",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.FillBounds
            )
            Image(painter = painterResource(id = playVideoButtonId),
                contentDescription = "Play video button",
                modifier = Modifier
                    .size(70.dp)
                    .alpha(0f)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.loading_in_progress),
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 24.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
@Preview
fun LibraryPreview(){
    TipScreen(
        isLoading = false,
        displayMessage = "Somethign Test",
        displaySoundButton = true,
        displayVideoButton = true,
        displayBackButton = false,
        displayNext = true,
        backgroundId = R.drawable.library_bg,
        playSoundButtonId = R.drawable.library_playaudio,
        screenIconId = R.drawable.library_icon,
        playVideoButtonId = R.drawable.library_playvideo ,
        previousButtonID = R.drawable.library_prev,
        nextButtonID = R.drawable.library_next ,
        isFavorite = false, videoFileName = "",
        onVideoButtonClicked = {},
        mediaPlayer = null,
        pauseSoundButtonId = R.drawable.rectangle_1_3x
    , onNextPressed = {}, onPrevPressed = {}, onBackPressed = {}) {

    }
}