package com.learning.leap.bwb.vote

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.learning.leap.bwb.R
import com.learning.leap.bwb.baseActivity.HomeActivity
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.library.VideoActivity
import com.learning.leap.bwb.utility.Constant
import com.learning.leap.bwb.utility.Utility
import java.io.File
import java.io.FileInputStream

class VoteViewActivity : AppCompatActivity(), VoteViewViewInterface,
    MediaPlayer.OnPreparedListener {
    lateinit var votePresenter: VotePresenter
    var mediaPlayer: MediaPlayer? = null
    var isPlaying = false
    var favoirte: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_vote)
        mediaPlayer = MediaPlayer()
        val bucketNumber = intent.getIntExtra("BucketNumber", 1)
        val numberOfTips = intent.getIntExtra("NumberOfTips", 1)
        votePresenter = VotePresenter(numberOfTips, bucketNumber, this)
        votePresenter.onCreate()
        setOnClickListener()
        mediaPlayer!!.setOnCompletionListener {
            releaseMediaPlayer()
            isPlaying = false
        }
    }

    override fun hideFavoriteButton() {
       // voteFragmentFavoriteButton.visibility = View.GONE
    }

    private fun setOnClickListener() {
//        voteFragmentPlayAudioImageView.setOnClickListener { votePresenter.onPlayAudioPress() }
//        voteFragmentPlayVideoImageView.setOnClickListener { votePresenter.onPlayVideoPress() }
//        voteFragmentThumbsDownImageView.setOnClickListener {
//            Utility.addCustomEvent(
//                Constant.THUMBS_DOWN,
//                Utility.getUserID(this),
//                votePresenter.tag,
//                this
//            )
//            votePresenter.thumbDownButtonTapped()
//        }
//        voteFragmentThumbsUpImageView.setOnClickListener {
//            Utility.addCustomEvent(
//                Constant.THUMBS_UP,
//                Utility.getUserID(this),
//                votePresenter.tag,
//                this
//            )
//            votePresenter.thumbUpButtonTapped()
//        }
//        voteFragmentStopButton.setOnClickListener { votePresenter.onStopButtonPress() }
//        voteFragmentFavoriteButton.setOnClickListener {
//            favoirte = votePresenter.updateFavoriteForTip()
//            updateFavorite(favoirte)
//        }
    }

    override fun onPrepared(player: MediaPlayer) {
        player.start()
        isPlaying = true
    }

    private fun releaseMediaPlayer() {
        if (isPlaying) {
            mediaPlayer!!.stop()
            isPlaying = false
        }
        mediaPlayer!!.release()
        mediaPlayer = MediaPlayer()
    }

    override fun updateFavorite(isFavorite: Boolean) {
        favoirte = isFavorite
//        if (isFavorite) {
//            voteFragmentFavoriteButton.setImageResource(R.drawable.heartfilled)
//        } else {
//            voteFragmentFavoriteButton.setImageResource(R.drawable.heart)
//        }
    }

    override fun homeIntent() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(homeIntent)
        finish()
    }

    override fun displayPrompt(prompt: String) {
        Utility.addCustomEvent(
            Constant.VIEWED_NOTIFICATIONS,
            Utility.getUserID(this),
            votePresenter.tag,
            this
        )
        //voteFragmentPromptTextView.text = prompt
    }

    override fun playSound(fileName: String) {
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
            Constant.PLAYED_SOUND,
            Utility.getUserID(this),
            votePresenter.tag,
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
            votePresenter.tag,
            this
        )
        VideoActivity.showRemoteVideo(this, fileName)
    }

    override fun displaySoundButton() {
        //voteFragmentPlayAudioImageView.visibility = View.VISIBLE
    }

    override fun displayVideoButton() {
       // voteFragmentPlayVideoImageView.visibility = View.VISIBLE
    }

    override fun hideSoundButton() {
        //voteFragmentPlayAudioImageView.visibility = View.INVISIBLE
    }

    override fun hideVideoButton() {
        //voteFragmentPlayVideoImageView.visibility = View.INVISIBLE
    }

    override fun hideStopButton() {
       // voteFragmentStopButton.visibility = View.GONE
    }

    override fun displayStopButton() {
        //voteFragmentStopButton.visibility = View.VISIBLE
    }

    override fun stopPlayer() {
        releaseMediaPlayer()
    }

    override fun babyName(): String {
        val localLoadSaveHelper = LocalLoadSaveHelper(this)
        return localLoadSaveHelper.babyName
    }
}