package com.learning.leap.bwb.download

import android.icu.util.Calendar
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.model.AmazonS3Exception
import com.learning.leap.bwb.localNotification.TipNotification
import com.learning.leap.bwb.localNotification.TipScheduler
import com.learning.leap.bwb.room.BabbleDatabase
import com.learning.leap.bwb.utility.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val transferUtility: TransferUtility,
    private val database: BabbleDatabase,
    private val fileDir: File,
    private val dataStore: DataStore<Preferences>,
    private val tipScheduler: TipScheduler
) : ViewModel() {
    private var totalFilesToDownload = 0
    private var filesDownloaded = 0
    private var filesList = mutableListOf<String>()

    companion object {
        private const val NO_FILE = "no file"
        private const val BUCKET_NAME = "leapbtob"
    }

    private val _downloadState: MutableStateFlow<DownloadScreenState> =
        MutableStateFlow(DownloadScreenState.Starting)
    val downloadState: StateFlow<DownloadScreenState> = _downloadState

    init {
        downloadTips()
    }

    fun scheduleTips() {
        val firstTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            this.add(Calendar.DATE, 1)
        }.timeInMillis

        val secondTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            this.add(Calendar.DATE, 1)
        }.timeInMillis
        viewModelScope.launch {
            dataStore.edit {
                it[intPreferencesKey(Constant.TIP_ONE_HOURS)] = 6
                it[intPreferencesKey(Constant.TIP_TWO_HOURS)] = 17
                it[intPreferencesKey(Constant.TIP_ONE_MINS)] = 0
                it[intPreferencesKey(Constant.TIP_TWO_MINS)] = 0
                it[booleanPreferencesKey(Constant.TIP_ONE_ON)] = true
                it[booleanPreferencesKey(Constant.TIP_TWO_ON)] = true
                it[intPreferencesKey(Constant.NUM_OF_TIPS)] = 2
            }
        }
        tipScheduler.schedule(TipNotification(firstTime, 2, 1))
        tipScheduler.schedule(TipNotification(secondTime, 2, 2))
    }


    private fun downloadTips() {
        viewModelScope.launch {
            filesList.addAll(retrieveFilesToDownload())
            val fileName = filesList[filesDownloaded]
            val file = File(fileDir, filesList[filesDownloaded])
            totalFilesToDownload = filesList.size
            downloadTip(fileName, file)
        }

    }

    private fun downloadTip(fileName: String, file: File) {
        val observer = transferUtility.download(
            BUCKET_NAME, fileName, file
        )
        observer.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                if (state == TransferState.COMPLETED) {
                    filesDownloaded++
                    _downloadState.value = DownloadScreenState.Downloading(getProgress())
                    downloadNextItem()
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}

            override fun onError(id: Int, ex: Exception?) {
                if (ex is AmazonS3Exception && ex.statusCode != 404) {
                    _downloadState.value = DownloadScreenState.Error
                } else {
                    ex?.printStackTrace()
                    filesDownloaded++
                    downloadNextItem()
                }
            }

        })
    }

    fun downloadNextItem() {
        if (totalFilesToDownload == filesDownloaded) {
            viewModelScope.launch {
                dataStore.edit { pref ->
                    pref[booleanPreferencesKey(Constant.DID_DOWNLOAD)] = true
                }
                _downloadState.value = DownloadScreenState.Success
            }
        } else {
            downloadTip(filesList[filesDownloaded], File(fileDir, filesList[filesDownloaded]))
        }
    }

    private suspend fun retrieveFilesToDownload(): List<String> {
        val allTips = database.babbleTipDAO().getAll()
        val soundFiles = allTips.filter {
            it.soundFileName != NO_FILE
        }.map {
            it.created + "-" + it.soundFileName
        }

        val videoFiles = allTips.filter {
            it.videoFileName != NO_FILE
        }.map {
            it.created + "-" + it.videoFileName
        }
        return soundFiles + videoFiles
    }

    private fun getProgress(): Int {
        val progress = filesDownloaded.toDouble() / totalFilesToDownload.toDouble()
        return (progress * 100).roundToInt()
    }

    fun retryDownload() {
        downloadTips()
    }

}