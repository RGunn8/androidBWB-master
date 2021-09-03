package com.learning.leap.bwb

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.learning.leap.bwb.userInfo.UserInfoActivity
import com.learning.leap.bwb.utility.Constant
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_update_two.*

class UpdatedToTwoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_two)
        supportActionBar?.hide()
        updateContinueButton.setOnClickListener {
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            realm.deleteAll()
            realm.commitTransaction()
            userInfoIntent()
        }

    }

    private fun userInfoIntent() {
        val userInfoIntent = Intent(this, UserInfoActivity::class.java)
        userInfoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        userInfoIntent.putExtra(Constant.NEW_USER, true)
        startActivity(userInfoIntent)
    }
}