package com.learning.leap.bwb.settings

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.learning.leap.bwb.R
import com.learning.leap.bwb.baseActivity.DetailActivity
import com.learning.leap.bwb.userInfo.UserInfoActivity
import kotlinx.android.synthetic.main.activity_setting_option.*

class SettingOptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_option)
        setUpSettingRecyclerView()
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.dark_green
                )
            )
        )
        supportActionBar?.title = "Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    private fun setUpSettingRecyclerView() {
        val titles = resources.getStringArray(R.array.setting_titles)
        val detailStrings = resources.getStringArray(R.array.setting_detail)
        val adapter = SettingAdapter(titles.toList(), detailStrings.toList())
        settingsRecyclerView.adapter = adapter
        settingsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        settingsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter.itemOnClick = {
            when (it) {
                0 -> userSettingsIntent()
                1 -> tipsIntent()
                else -> {
                    ageRangeSettingsIntent()
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId: Int = item.itemId
        if (itemId == android.R.id.home) {
            finish()
        }
        return true

    }


    private fun userSettingsIntent() {
        val userSettingIntent = Intent(this@SettingOptionActivity, UserInfoActivity::class.java)
        userSettingIntent.putExtra("newUser", false)
        startActivity(userSettingIntent)
    }

    private fun ageRangeSettingsIntent() {
        val ageRangeIntent = Intent(this@SettingOptionActivity, AgeRangeActivity::class.java)
        startActivity(ageRangeIntent)
    }

    private fun tipsIntent() {
        val tipSettingsIntent = Intent(this@SettingOptionActivity, TipSettingsActivity::class.java)
        startActivity(tipSettingsIntent)
    }

    private fun libraryIntent() {
        val detailIntent = Intent(this@SettingOptionActivity, DetailActivity::class.java)
        detailIntent.putExtra(DetailActivity.DETAIL_INTENT, DetailActivity.LIBRARY)
        startActivity(detailIntent)
    }

    private fun playTodayIntent() {
        val detailIntent = Intent(this@SettingOptionActivity, DetailActivity::class.java)
        detailIntent.putExtra(DetailActivity.DETAIL_INTENT, DetailActivity.PLAY_TODAY)
        startActivity(detailIntent)
    }

    private fun homeIntent() {
        finish()
    }
}