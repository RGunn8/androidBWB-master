@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.learning.leap.bwb.settings.ageRange

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learning.leap.bwb.BackButton
import com.learning.leap.bwb.R
import com.learning.leap.bwb.destinations.DownloadScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun AgeRangeScreen(
    viewModel: AgeRangeViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val currentIndex = viewModel.currentIndex.collectAsStateWithLifecycle()
    val goToDownload = viewModel.goToDownloadPage.collectAsStateWithLifecycle()
    val showSaveButton = viewModel.showSaveButton.collectAsStateWithLifecycle()
    if (goToDownload.value) {
        navigator.navigate(DownloadScreenDestination(fromAgeRange = true))
        viewModel.setGoDownloadToFalse()

    }
    Column(modifier = Modifier.fillMaxSize()) {
        BackButton {
            navigator.popBackStack()
        }
        AgeRangeContent(currentIndex.value, showSaveButton.value, {
            viewModel.getNewTips()
        }) {
            viewModel.updateBucketIndex(it)
        }

    }
}

@Composable
private fun AgeRangeContent(
    index: Int,
    showSaveButton: Boolean,
    onSaveClicked: () -> Unit,
    indexUpdated: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val ageRangeList = listOf(
        "Months 0 to 3",
        "Months 4 to 6",
        "Months 7 to 9",
        "Months 10 to 12",
        "Months 13 to 18",
        "Months 19 to 24",
        "Months 25 to 30",
        "Months 31 to 36",
        "Months 37 to 48"
    )
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.setting_bg),
            contentDescription = "Setting Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Select Textfield to get new age appropriate tips",
                modifier = Modifier.padding(bottom = 24.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                TextField(
                    // The `menuAnchor` modifier must be passed to the text field for correctness.
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),// <------ADD menuAnchor
                    readOnly = true,
                    value = ageRangeList[index],
                    onValueChange = {},
                    label = { Text("Age Range") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }) {
                    ageRangeList.forEachIndexed { index, username ->
                        DropdownMenuItem(text = {
                            Text(text = username)
                        },
                            onClick = {
                                expanded = false
                                indexUpdated.invoke(index)
                            })
                    }
                }


            }

            if (showSaveButton) {
                Image(
                    painterResource(R.drawable.settings_save),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSaveClicked.invoke()
                        })
            }
        }
    }

}