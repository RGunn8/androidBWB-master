package com.learning.leap.bwb.userInfo

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.learning.leap.bwb.R
import com.learning.leap.bwb.destinations.DownloadScreenDestination
import com.learning.leap.bwb.theme.BabbleTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.*

@Composable
@Destination
fun UserSettingsScreen(newUser: Boolean, viewModel: UserSettingViewModel = hiltViewModel(), navigator: DestinationsNavigator) {
    val state: UserSettingState by viewModel.state.observeAsState(UserSettingState())
    val showDialog: Boolean by viewModel.showDialog.observeAsState(false)
    val saveSuccessful: Boolean by viewModel.saveSuccessful.observeAsState(false)

    val activity = (LocalContext.current as? Activity)
    BabbleTheme {
        if (saveSuccessful){
            if (newUser){
                navigator.navigate(DownloadScreenDestination)
            }else{
                navigator.navigateUp()
            }
        }

        BackHandler() {
            if (newUser){
                activity?.finish()
            }
        }
        UserSettingContent(
            state = state,
            showDialog = showDialog,
            onBirthdayChanged = { viewModel.onBirthdayChanged(it) },
            onNameChanged = { viewModel.onNameChange(it) },
            onNewGenderClick = { viewModel.onGenderChange(it) },
            onCodeChanged = { viewModel.onCodeChange(it) },
            onSettingSaveClicked = { viewModel.saveBabbleUser(newUser) },
            dismissedDialog = {viewModel.resetError()},

        )
    }
}

@Composable
fun GenderButton(
    stateGender: Gender,
    buttonGender: Gender,
    stringId: Int,
    modifier: Modifier,
    onGenderClick: (Gender) -> Unit
) {
    Button(
        modifier = modifier,
        onClick = { onGenderClick(buttonGender) },
        border = BorderStroke(2.dp, colorResource(id = R.color.darkestBlue)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White,
            backgroundColor = if (stateGender == buttonGender) {
                colorResource(
                    id = R.color.darkestBlue
                )
            } else {
                colorResource(
                    id = R.color.light_grey
                )
            }
        )
    ) {
        Text(stringResource(id = stringId))
    }
}

@Composable
fun DateTextField(
    onBirthdayChanged: (String) -> Unit,
    context:Context
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed: Boolean by interactionSource.collectIsPressedAsState()

    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val date = remember { mutableStateOf("") }
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            date.value = "$month/$dayOfMonth/$year"
            onBirthdayChanged(date.value)
        }, year, month, day
    )

    if (isPressed) {
        datePickerDialog.show()
    }

    OutlinedTextField(
        modifier = Modifier
            .background(
                Color.White
            )
            .focusable(),
        interactionSource = interactionSource,
        value = date.value,
        singleLine = true,
        enabled = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        placeholder = { Text(stringResource(id = R.string.userBirthdayHint)) },
        onValueChange = onBirthdayChanged,
    )
}

@Composable
fun SimpleAlertDialog(errorTitle:Int, errorMessage:Int, showDialog: Boolean,onConfirmPressed: () -> Unit, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirmPressed)
                { Text(text = "OK") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss)
                { Text(text = "Cancel") }
            },
            title = { Text(stringResource(id = errorTitle)) },
            text = { Text(stringResource(id = errorMessage)) }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun UserSettingContent(
    state: UserSettingState = UserSettingState(),
    showDialog: Boolean = false,
    onNewGenderClick: (Gender) -> Unit = {},
    onBirthdayChanged: (String) -> Unit = {},
    onNameChanged: (String) -> Unit = {},
    onCodeChanged: (String) -> Unit = {},
    onSettingSaveClicked: () -> Unit = {},
    dismissedDialog: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current

    Scaffold() {
        it.calculateBottomPadding()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(enabled = true, state = rememberScrollState())
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
        ) {
            Image(
                painter = painterResource(id = R.drawable.setting_bg),
                contentDescription = "setting_background",
                modifier = Modifier.fillMaxSize()
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize(),
            ) {
                Spacer(Modifier.height(100.dp))
                DateTextField(
                    onBirthdayChanged = onBirthdayChanged,
                    context
                )
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .background(
                            Color.White
                        ),
                    value = state.userName,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }),
                    placeholder = { Text(stringResource(id = R.string.userFirstNameHint)) },
                    onValueChange = onNameChanged,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    GenderButton(
                        modifier = Modifier.weight(1f),
                        stateGender = state.gender,
                        buttonGender = Gender.MALE,
                        stringId = R.string.male,
                        onGenderClick = { onNewGenderClick(it) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    GenderButton(
                        modifier = Modifier.weight(1f),
                        stateGender = state.gender,
                        buttonGender = Gender.FEMALE,
                        stringId = R.string.female,
                        onGenderClick = { onNewGenderClick(it) }
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    GenderButton(
                        modifier = Modifier.weight(1f),
                        stateGender = state.gender,
                        buttonGender = Gender.NOT_NOW,
                        stringId = R.string.not_now,
                        onGenderClick = { onNewGenderClick(it) }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = stringResource(id = R.string.userRequestInfo))
                Text(
                    text = stringResource(id = R.string.userTapHereFroMore),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .background(
                            Color.White
                        ),
                    value = state.groupCode,
                    singleLine = true,
                    placeholder = { Text(stringResource(id = R.string.optional_group_code)) },
                    onValueChange = onCodeChanged,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    painterResource(id = R.drawable.settings_save),
                    contentDescription = "",
                    Modifier
                        .background(Color.Transparent)
                        .clickable(onClick = onSettingSaveClicked)
                )
                SimpleAlertDialog(state.error.errorTitle,state.error.errorMessage,showDialog,dismissedDialog,dismissedDialog)
            }
        }

    }
}