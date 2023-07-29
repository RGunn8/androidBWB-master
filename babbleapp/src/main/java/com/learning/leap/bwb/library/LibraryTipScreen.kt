package com.learning.leap.bwb.library

import android.content.Context
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learning.leap.bwb.BackButton
import com.learning.leap.bwb.R
import com.learning.leap.bwb.model.BabbleTip
import com.learning.leap.bwb.tip.TipScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.io.File
import java.io.FileInputStream

@Composable
@Destination
fun LibraryTipScreen(navigator: DestinationsNavigator,libraryScreenType: LibraryScreenType,categoryName:String = "",viewModel: LibraryScreenViewModel = hiltViewModel()){
    val context = LocalContext.current
    LaunchedEffect(key1 = libraryScreenType, block = {
        viewModel.getTips(libraryScreenType,categoryName)
    }
    )
    var currentIndex by remember {
        mutableIntStateOf(0)
    }
    val tipList = viewModel.tipList.collectAsStateWithLifecycle()
    val babyName = viewModel.babyName.collectAsStateWithLifecycle()
    var currentTip by remember {
        mutableStateOf(BabbleTip())
    }
        var isFavorite by remember {
            mutableStateOf(currentTip.favorite)
        }
        Column {
            BackButton {
                navigator.popBackStack()
            }
            currentTip = if (tipList.value.isNotEmpty()){
                tipList.value[currentIndex]
            }else{
                BabbleTip()
            }

            TipScreen(
                isLoading = tipList.value.isEmpty(),
                displayMessage = currentTip.updateMessage(babyName.value),
                displaySoundButton = !currentTip.noSoundFile(),
                displayVideoButton = !currentTip.noVideFile(),
                videoFileName = currentTip.getVideoFile(),
                displayBackButton = tipList.value.isNotEmpty() && currentIndex > 0,
                displayNext = currentIndex != tipList.value.size - 1,
                backgroundId = R.drawable.library_bg,
                playSoundButtonId = R.drawable.library_playaudio,
                pauseSoundButtonId = R.drawable.rectangle_1_3x,
                screenIconId = R.drawable.library_icon,
                playVideoButtonId = R.drawable.library_playvideo,
                previousButtonID = R.drawable.library_prev,
                nextButtonID = R.drawable.library_next,
                mediaPlayer = CreateMediaPlayer(
                    context = context,
                    fileName = currentTip.getSoundFile()
                ),
                isFavorite = isFavorite,
                onNextPressed = {
                    if (currentIndex != tipList.value.size - 1) {
                        currentIndex++
                        currentTip = tipList.value[currentIndex]
                    }
                }, onPrevPressed = {
                    if (currentIndex != 0){
                        currentIndex--
                        currentTip = tipList.value[currentIndex]
                    }
                }, onBackPressed = {
                    navigator.popBackStack()
                }
            ) {
                isFavorite = !isFavorite
                viewModel.updateTipFavorite(currentTip, isFavorite)
            }
        }
}

@Composable
fun CreateMediaPlayer(context: Context, fileName: String): MediaPlayer? {
    return try {
        if (fileName.isNotEmpty()) {
            val file = File(context.filesDir, fileName)
            val mp = MediaPlayer()
            val fs = FileInputStream(file)
            mp.setDataSource(fs.fd)
            mp
        } else {
            null
        }
    }catch (ex : Exception){
        null
    }
}