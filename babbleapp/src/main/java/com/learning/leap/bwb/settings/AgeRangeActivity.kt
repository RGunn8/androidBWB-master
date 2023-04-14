package com.learning.leap.bwb.settings

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.learning.leap.bwb.R
import com.learning.leap.bwb.download.DownloadActivity
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.utility.Constant
import com.learning.leap.bwb.utility.Utility

class AgeRangeActivity : AppCompatActivity() {
    lateinit var adapter: AgeRangeAdapter
    var ageRangeBucket = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_age_range)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.dark_green
                )
            )
        )
        supportActionBar?.title = "Age Range Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val saveHelper = LocalLoadSaveHelper(this)
        ageRangeBucket = saveHelper.ageRangeBucketNumber
        setUpRecyclerView()
//        ageRangeSaveButton.setOnClickListener {
//            saveHelper.saveAgeRangeBucket(adapter.selectPosition)
//            Utility.writeBoolenSharedPreferences(Constant.DID_DOWNLOAD, false, this)
//            downloadIntent()
//        }
    }

    private fun setUpRecyclerView() {
        val ageRanges = resources.getStringArray(R.array.age_ranges)
        adapter = AgeRangeAdapter(ageRanges.toList(), this)
        adapter.selectPosition = ageRangeBucket
//        ageRangeRecyclerView.adapter = adapter
//        ageRangeRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter.notifyItemChanged(ageRangeBucket)
        adapter.itemOnClick = {
            Log.d("test", it.toString())
            val oldSelectedPosition = adapter.selectPosition
            if (oldSelectedPosition != it) {
                adapter.selectPosition = it
                adapter.notifyItemChanged(it)
                adapter.notifyItemChanged(oldSelectedPosition)
                if (ageRangeBucket != adapter.selectPosition) {
                   // ageRangeSaveButton.visibility = View.VISIBLE
                } else {
                    //ageRangeSaveButton.visibility = View.GONE
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


    private fun downloadIntent() {
        val updateIntent = Intent(this, DownloadActivity::class.java)
        updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        updateIntent.putExtra(Constant.COME_FROM_AGE_RANGE, true)
        updateIntent.putExtra(Constant.UPDATE, true)
        startActivity(updateIntent)

    }

}