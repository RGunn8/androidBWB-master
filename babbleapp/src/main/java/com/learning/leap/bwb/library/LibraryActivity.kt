package com.learning.leap.bwb.library

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.learning.leap.bwb.R
import com.learning.leap.bwb.baseActivity.HomeActivity
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.notification.NotificaitonPresenter
import com.learning.leap.bwb.notification.NotificationViewViewInterface
import com.learning.leap.bwb.settings.SettingOptionActivity
import com.learning.leap.bwb.utility.Constant
import com.learning.leap.bwb.utility.Utility
import kotlinx.android.synthetic.main.activity_library.*
import java.io.File
import java.io.FileInputStream

class LibraryActivity : AppCompatActivity(), NotificationViewViewInterface,
    MediaPlayer.OnPreparedListener {
    lateinit var notificationPresenter: NotificaitonPresenter
    var mediaPlayer: MediaPlayer? = null
    var favoirte: Boolean = false
    var isPlaying = false

    companion object {
        val IS_ALL = "IS_ALL"
        val IS_CATEGORY = "IS_CATEGORY"
        val IS_FAVORITE = "IS_FAVORITE"
        val IS_SUB_CATEGORY = "IS_SUBCATEGORY"
        val LIBRARY_CATEGORY = "LIBRARY_CATEGORY"
        val LIBRARY_SUB_CATEGORY = "LIBRARY_SUB_CATEGORY"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId: Int = item.itemId
        if (itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    override fun hideFavoriteButton() {
        libraryActivityFavoriteButton.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.lipstick
                )
            )
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Library"
        notificationPresenter = NotificaitonPresenter()
        notificationPresenter.attachView(this)
        if (intent.getBooleanExtra(IS_ALL, false)) {
            notificationPresenter.isAll = true
        } else if (intent.getBooleanExtra(IS_CATEGORY, false)) {
            notificationPresenter.isAll = false
            notificationPresenter.isCategory = true
            notificationPresenter.category = intent.getStringExtra(LIBRARY_CATEGORY)
        } else if (intent.getBooleanExtra(IS_FAVORITE, false)) {
            notificationPresenter.isAll = false
            notificationPresenter.isFavorite = true
        } else {
            notificationPresenter.isAll = false
            notificationPresenter.isSubCategory = true
            notificationPresenter.subCategory = intent.getStringExtra(LIBRARY_SUB_CATEGORY)
        }
        notificationPresenter.onCreate()
        notificationPresenter.displayFavorite()
        mediaPlayer = MediaPlayer()

        libraryActivityNextImageView.setOnClickListener { notificationPresenter.onNextPress() }
        libraryActivityPreviousImageView.setOnClickListener { notificationPresenter.onBackPress() }
        libraryActivityPlayAudioImageView.setOnClickListener { notificationPresenter.onPlayAudioPress() }
        mediaPlayer!!.setOnCompletionListener { mediaPlayer1: MediaPlayer? ->
            releaseMediaPlayer()
            isPlaying = false
        }
        libraryActivityPlayVideoImageView.setOnClickListener { notificationPresenter.onPlayVideoPress() }
        libraryActivityStopButton.setOnClickListener { notificationPresenter.onStopButtonPress() }
        libraryActivityFavoriteButton.setOnClickListener {
            favoirte = notificationPresenter.updateFavoriteForTip()
            updateFavorite(favoirte)
        }
    }

    private fun releaseMediaPlayer() {
        if (isPlaying) {
            mediaPlayer!!.stop()
            isPlaying = false
        }
        mediaPlayer!!.release()
        mediaPlayer = MediaPlayer()
    }

    override fun onPrepared(player: MediaPlayer) {
        player.start()
        isPlaying = true
    }

    override fun onDestroy() {
        notificationPresenter.onDestory()
        super.onDestroy()
    }

    override fun updateFavorite(isFavorite: Boolean) {
        favoirte = isFavorite
        if (isFavorite) {
            libraryActivityFavoriteButton.setImageResource(R.drawable.heartfilled)
        } else {
            libraryActivityFavoriteButton.setImageResource(R.drawable.heart)
        }
    }

    override fun displayPrompt(prompt: String) {
        libraryActivityPromptTextView.text = prompt
    }

    override fun playSound(fileName: String) {
        Utility.addCustomEvent(
            Constant.PLAYED_SOUND,
            Utility.getUserID(this),
            notificationPresenter.tag,
            this
        )
        try {
            setupMediaFile(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }

    @Throws(Exception::class)
    private fun setupMediaFile(fileName: String) {
        Utility.addCustomEvent(
            Constant.VIEWED_NOTIFICATIONS,
            Utility.getUserID(this),
            notificationPresenter.tag,
            this
        )
        val file = File(this.filesDir, fileName)
        val `is` = FileInputStream(file)
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.setDataSource(`is`.fd)
        `is`.close()
        mediaPlayer!!.setOnPreparedListener(this)
        mediaPlayer!!.prepareAsync()
    }

    override fun playVideo(fileName: String) {
        Utility.addCustomEvent(
            Constant.PLAYED_VIDEO,
            Utility.getUserID(this),
            notificationPresenter.tag,
            this
        )
        VideoActivity.showRemoteVideo(this, fileName)
    }

    override fun stopPlayer() {
        releaseMediaPlayer()
    }

    override fun hideNextButton() {
        libraryActivityNextImageView.visibility = View.INVISIBLE
    }

    override fun hidePreviousButton() {
        libraryActivityPreviousImageView.visibility = View.INVISIBLE
    }

    override fun hideSoundButton() {
        libraryActivityPlayAudioImageView.visibility = View.INVISIBLE
    }

    override fun hideVideoButton() {
        libraryActivityPlayVideoImageView.visibility = View.INVISIBLE
    }

    override fun displayNextButton() {
        libraryActivityNextImageView.visibility = View.VISIBLE
    }

    override fun displayPreviousButton() {
        libraryActivityPreviousImageView.visibility = View.VISIBLE
    }

    override fun displaySoundButton() {
        libraryActivityPlayAudioImageView.visibility = View.VISIBLE
    }

    override fun displayVideoButton() {
        libraryActivityPlayVideoImageView.visibility = View.VISIBLE
    }

    override fun onHomePress() {
//        val homeIntent = Intent(activity, HomeActivity::class.java)
//        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        startActivity(homeIntent)
    }

    override fun onSettingsPress() {
//        val settingsIntent = Intent(activity, SettingOptionActivity::class.java)
//        startActivity(settingsIntent)
    }

    override fun onPlayToday() {
//        val ft = activity!!.supportFragmentManager.beginTransaction()
//        val playTodayFragment = PlayTodayFragment()
//        ft.replace(R.id.detailFragment, playTodayFragment)
//        ft.commit()
    }

    override fun onLibraryPress() {}
    override fun hideStopButton() {
        libraryActivityStopButton.visibility = View.GONE
    }

    override fun displayStopButton() {
        libraryActivityStopButton.visibility = View.VISIBLE
    }

    override fun babyName(): String {
        val localLoadSaveHelper = LocalLoadSaveHelper(this)
        return localLoadSaveHelper.babyName
    }
}