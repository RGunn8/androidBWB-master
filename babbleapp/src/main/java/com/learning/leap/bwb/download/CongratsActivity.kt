package com.learning.leap.bwb.download

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.learning.leap.bwb.R
import com.learning.leap.bwb.baseActivity.HomeActivity
import kotlinx.android.synthetic.main.activity_congrats.*

class CongratsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congrats)
        congratContinueButton.setOnClickListener {
            val congratsIntent = Intent(this, HomeActivity::class.java)
            congratsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(congratsIntent)
        }
    }
}