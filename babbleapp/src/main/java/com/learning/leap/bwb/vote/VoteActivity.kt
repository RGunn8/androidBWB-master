package com.learning.leap.bwb.vote

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.learning.leap.bwb.library.screens.VoteScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        setContent {
            VoteScreen {

            }
        }
    }

}