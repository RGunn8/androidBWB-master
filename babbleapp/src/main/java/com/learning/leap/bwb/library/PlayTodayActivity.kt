package com.learning.leap.bwb.library

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.learning.leap.bwb.PlayTodayPresenter
import com.learning.leap.bwb.R
import com.learning.leap.bwb.baseActivity.HomeActivity
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.notification.NotificationViewViewInterface
import com.learning.leap.bwb.settings.SettingOptionActivity
import com.learning.leap.bwb.utility.Constant
import com.learning.leap.bwb.utility.Utility
import kotlinx.android.synthetic.main.activity_library.*
import kotlinx.android.synthetic.main.fragment_play_today.*
import java.io.File
import java.io.FileInputStream

class PlayTodayActivity : AppCompatActivity(), MediaPlayer.OnPreparedListener,
    NotificationViewViewInterface {

    var mediaPlayer: MediaPlayer? = null
    var favoirte: Boolean = false
    var isPlaying = false
    lateinit var presenter: PlayTodayPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_play_today)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpOnClickListener()
        presenter = PlayTodayPresenter()
        presenter.attachView(this)
        presenter.onCreate()
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setOnCompletionListener {
            releaseMediaPlayer()
            isPlaying = false
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId: Int = item.itemId
        if (itemId == android.R.id.home) {
            finish()
        }
        return true

    }


    override fun hideFavoriteButton() {
        playTodayFragmentFavoriteButton.visibility = View.GONE
    }

    override fun updateFavorite(isFavorite: Boolean) {
        favoirte = isFavorite
        if (isFavorite) {
            playTodayFragmentFavoriteButton.setImageResource(R.drawable.heartfilled)
        } else {
            playTodayFragmentFavoriteButton.setImageResource(R.drawable.heart)
        }
    }

    private fun setUpOnClickListener() {
        playTodayFragmentPlayVideoImageView.setOnClickListener { presenter.onPlayVideoPress() }
        playTodayFragmentPlayAudioImageView.setOnClickListener { presenter.onPlayAudioPress() }
        playTodayFragmentNextImageView.setOnClickListener { presenter.onNextPress() }
        playTodayFragmentPreviousImageView.setOnClickListener { presenter.onBackPress() }
        playTodayFragmentStopButton.setOnClickListener { presenter.onStopButtonPress() }
        playTodayFragmentFavoriteButton.setOnClickListener {
            favoirte = presenter.updateFavoriteForTip()
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

    override fun hideNextButton() {
        playTodayFragmentNextImageView.visibility = View.INVISIBLE
    }

    override fun hidePreviousButton() {
        playTodayFragmentPreviousImageView.visibility = View.INVISIBLE
    }

    override fun displayNextButton() {
        playTodayFragmentNextImageView.visibility = View.VISIBLE
    }

    override fun displayPreviousButton() {
        playTodayFragmentPreviousImageView.visibility = View.VISIBLE
    }

    override fun displayPrompt(prompt: String) {
        playTodayFragmentPromptTextView.text = prompt
    }

    override fun playSound(fileName: String) {
        Utility.addCustomEvent(Constant.PLAYED_SOUND, Utility.getUserID(this), presenter.tag, this)
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
            presenter.tag,
            this
        )
        val file = File(filesDir, fileName)
        val `is` = FileInputStream(file)
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.setDataSource(`is`.fd)
        `is`.close()
        mediaPlayer!!.setOnPreparedListener(this)
        mediaPlayer!!.prepareAsync()
    }

    override fun playVideo(fileName: String) {
        Utility.addCustomEvent(Constant.PLAYED_VIDEO, Utility.getUserID(this), presenter.tag, this)
        VideoActivity.showRemoteVideo(this, fileName)
    }

    override fun displaySoundButton() {
        playTodayFragmentPlayAudioImageView.visibility = View.VISIBLE
    }

    override fun displayVideoButton() {
        playTodayFragmentPlayVideoImageView.visibility = View.VISIBLE
    }

    override fun hideSoundButton() {
        playTodayFragmentPlayAudioImageView.visibility = View.INVISIBLE
    }

    override fun hideVideoButton() {
        playTodayFragmentPlayVideoImageView.visibility = View.INVISIBLE
    }

    override fun onHomePress() {

    }

    override fun onSettingsPress() {

    }

    override fun onPlayToday() {}
    override fun onLibraryPress() {}
    override fun hideStopButton() {
        playTodayFragmentStopButton.visibility = View.GONE
    }

    override fun displayStopButton() {
        playTodayFragmentStopButton.visibility = View.VISIBLE
    }

    override fun stopPlayer() {
        releaseMediaPlayer()
    }

    override fun babyName(): String {
        val localLoadSaveHelper = LocalLoadSaveHelper(this)
        return localLoadSaveHelper.babyName
    }
}