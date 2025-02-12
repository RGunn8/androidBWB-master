package com.learning.leap.bwb.settings.tipRemidner

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.isPm
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learning.leap.bwb.BackButton
import com.learning.leap.bwb.R
import com.learning.leap.bwb.localNotification.TipScheduler
import com.learning.leap.bwb.theme.BabbleTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun TipReminderScreen(
    viewModel: TipReminderViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state = viewModel.tipReminderState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val firstTimePickerState = rememberTimePickerState(
        initialHour = state.value.firstReminderHour,
        initialMinute = state.value.firstReminderMin,
        is24Hour = false,
    )

    val secondTimePickerState = rememberTimePickerState(
        initialHour = state.value.secondReminderHour,
        initialMinute = state.value.secondReminderMin,
        is24Hour = false,
    )

    BabbleTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            BackButton {
                navigator.popBackStack()
            }
            TipReminderContent(
                firstSwitchEnabled = state.value.firstReminderSwitchEnabled,
                secondSwitchEnabled = state.value.secondReminderSwitchEnabled,
                onNumberOfTipUpdated = {
                    viewModel.updateNumberOfTips(it)
                },
                onSaveClicked = {
                    val tipScheduler = TipScheduler(context)
                    viewModel.saveChanges(tipScheduler)
                    navigator.popBackStack()
                },
                firstTimePickerState = firstTimePickerState,
                secondTimePickerState = secondTimePickerState,
                numberOfTip = state.value.numOfTips,
                onFirstSwitchCheckChanged = {
                    viewModel.updateSwitch(true, it)
                },
            ) {
                viewModel.updateSwitch(false, it)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipReminderContent(
    firstSwitchEnabled: Boolean,
    secondSwitchEnabled: Boolean,
    firstTimePickerState: TimePickerState,
    secondTimePickerState: TimePickerState,
    numberOfTip: Int,
    onSaveClicked: () -> Unit,
    onNumberOfTipUpdated: (Int) -> Unit,
    onFirstSwitchCheckChanged: (Boolean) -> Unit,
    onSecondSwitchCheckChanged: (Boolean) -> Unit,
) {
    Scaffold() {
        it.calculateBottomPadding()
        Box(
            modifier = Modifier
                .fillMaxSize()
        )
        Image(
            painter = painterResource(id = R.drawable.setting_bg),
            contentDescription = "setting_background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.settings_icon),
                contentDescription = "Setting Icon",
                contentScale = ContentScale.FillBounds, modifier = Modifier
                    .padding(top = 24.dp, bottom = 8.dp)
                    .size(80.dp)
            )

            Text("Set Tips Reminder Time", fontSize = 24.sp)
            ReminderRow(
                timePickerState = firstTimePickerState,
                switchEnabled = firstSwitchEnabled, onSwitchChanged = onFirstSwitchCheckChanged,

                )

            ReminderRow(
                timePickerState = secondTimePickerState,
                switchEnabled = secondSwitchEnabled,
                onSwitchChanged = onSecondSwitchCheckChanged,
            )

            Text("Number of Tips per reminder", fontSize = 24.sp)
            var sliderPosition by remember { mutableStateOf(numberOfTip.toFloat()) }
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = sliderPosition.roundToInt().toString(), fontSize = 24.sp)
                Slider(
                    modifier = Modifier.semantics { contentDescription = "Localized Description" },
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 1f..5f,
                    onValueChangeFinished = {
                        onNumberOfTipUpdated.invoke(sliderPosition.roundToInt())
                    },
                    steps = 3
                )
            }

            Image(
                painterResource(id = R.drawable.settings_save),
                contentDescription = "",
                Modifier
                    .background(Color.Transparent)
                    .clickable(onClick = onSaveClicked)

            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderRow(
    switchEnabled: Boolean,
    timePickerState: TimePickerState,
    onSwitchChanged: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        var showTimePicker by remember { mutableStateOf(false) }

        if (showTimePicker) {
            TimeDialog({
                showTimePicker = false
            }, { showTimePicker = false }, timePickerState)
        }
        val amPreview = if (timePickerState.isPm) "PM" else "AM"
        val hours =
            if (timePickerState.hour == 0) "12" else if (timePickerState.hour > 12) (timePickerState.hour - 12).toString() else timePickerState.hour.toString()
        val min =
            if (timePickerState.minute <= 9) "0${timePickerState.minute}" else timePickerState.minute.toString()
        Text(
            "$hours:$min $amPreview",
            modifier = Modifier
                .background(Color.White)
                .clickable {
                    showTimePicker = true
                }
                .size(width = 140.dp, height = 80.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .padding(horizontal = 30.dp), textAlign = TextAlign.Center,

            fontSize = 18.sp
        )

        Switch(
            switchEnabled,
            onSwitchChanged,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    timePickerState: TimePickerState
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Dialog Title")
        },
        text = {
            TimePicker(
                state = timePickerState
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Dismiss")
            }
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TipReminderPreview() {
    TipReminderContent(
        firstSwitchEnabled = true,
        secondSwitchEnabled = false,
        firstTimePickerState = rememberTimePickerState(
            initialHour = 6,
            initialMinute = 0,
            is24Hour = false,
        ),
        secondTimePickerState = rememberTimePickerState(
            initialHour = 17,
            initialMinute = 0,
            is24Hour = false,
        ),
        numberOfTip = 2,
        onSaveClicked = {},
        onNumberOfTipUpdated = {},
        onFirstSwitchCheckChanged = {},
        onSecondSwitchCheckChanged = {},
    )
}