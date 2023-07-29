package com.learning.leap.bwb

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BackButton(onBackPressed: () -> Unit){
    IconButton(onClick = {
        onBackPressed.invoke()
    }, modifier = Modifier.padding(8.dp)) {
        Icon(Icons.Filled.ArrowBack, "")
    }
}