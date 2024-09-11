package com.learning.leap.bwb.library.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learning.leap.bwb.BackButton
import com.learning.leap.bwb.R
import com.learning.leap.bwb.library.LibraryScreenType
import com.learning.leap.bwb.library.LibraryScreenViewModel
import com.learning.leap.bwb.model.BabbleTip
import com.learning.leap.bwb.tip.TipScreen

@Composable
fun VoteScreen(viewModel: LibraryScreenViewModel = hiltViewModel(), onBackPressed: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(Unit, block = {
        viewModel.getTips(LibraryScreenType.VOTE)
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
            onBackPressed.invoke()
            //navigator.popBackStack()
        }
        currentTip = if (tipList.value.isNotEmpty()) {
            tipList.value[currentIndex]
        } else {
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
            backgroundId = R.drawable.tip_bg,
            playSoundButtonId = R.drawable.tip_playaudio,
            pauseSoundButtonId = R.drawable.rectangle_3_3x,
            screenIconId = 0,
            playVideoButtonId = R.drawable.tip_playvideo,
            previousButtonID = R.drawable.tip_thumbsdown,
            nextButtonID = R.drawable.tip_thumbsup,
            mediaPlayer = CreateMediaPlayer(
                context = context,
                fileName = currentTip.getSoundFile()
            ),
            isFavorite = isFavorite,
            onNextPressed = {

            }, onPrevPressed = {

            },
            onVideoButtonClicked = {

            },
            onBackPressed = {
                //onBackPressed.invoke()

            }
        ) {
            isFavorite = !isFavorite
            viewModel.updateTipFavorite(currentTip, isFavorite)
        }
    }
}
