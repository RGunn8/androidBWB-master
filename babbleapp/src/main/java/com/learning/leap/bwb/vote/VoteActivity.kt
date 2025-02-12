package com.learning.leap.bwb.vote

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.learning.leap.bwb.baseActivity.SplashActivity
import com.learning.leap.bwb.library.screens.VoteScreen
import com.learning.leap.bwb.localNotification.TipScheduler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var numberOfTips = 1

        intent?.let {
            numberOfTips = it.getIntExtra(TipScheduler.NUM_OF_TIP, 1)
        }

        setContent {
            VoteScreen(numberOfTips = numberOfTips) {
                homeIntent()
            }
        }
    }

    private fun homeIntent() {
        val homeIntent = Intent(this, SplashActivity::class.java)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(homeIntent)
    }

}