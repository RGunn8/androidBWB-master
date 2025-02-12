package com.learning.leap.bwb.download

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.s3.model.AmazonS3Exception
import com.amplifyframework.core.Amplify
import com.learning.leap.bwb.R
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.model.BabbleTip
import com.learning.leap.bwb.room.BabbleDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random.Default.nextInt

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    val transferUtility: TransferUtility,
     val babbleDatabase: BabbleDatabase,
    val localLoadSaveHelper: LocalLoadSaveHelper,
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val Progress = "Progress"

    }
    private var totalCount = 0
    private var filesdownloaded = 0
    private val BUCKET_NAME = "leapbtob"
    private val filesToDownloads = ArrayList<String>()
    override suspend fun doWork(): Result {
        return try {
            val tips = babbleDatabase.babbleTipDAO().getAll()
            for (notification in tips) {
                if (notification.soundFileName != "no file") {
                    addFileToArray(notification.created, notification.soundFileName)
                }
                if (notification.videoFileName != "no file") {
                    addFileToArray(notification.created, notification.videoFileName)
                }
            }
            totalCount = filesToDownloads.size
            if (canDownload()) {
                for (i in 0 .. tips.size) {
                    val progressLiveData =  workDataOf(Progress to getProgress())
                    setProgress(progressLiveData)
                    downloadFiles(i) {
                         when (it) {
                            DownloadWorkerState.Done -> {
                                Result.success()
                            }
                            DownloadWorkerState.Error -> {
                                throw Exception()
                            }
                            is DownloadWorkerState.Progress -> {
                                Log.d("progress","this is the progress ${it.progress}")
                            }
                        }
                    }
                }
                Result.success()
            } else {
              Result.failure()
            }

        }catch (ex:Exception){
            ex.printStackTrace()
            Result.failure()
        }
    }

//    private fun updateNotifications() {
//        Utility.writeBoolenSharedPreferences(Constant.UPDATE, true, context)
//        val babblePlayer = loadBabblePlayerFromSharedPref(localLoadSaveHelper)
//        val disposable = Single.fromCallable {
//            babblePlayer.retrieveNotifications(
//                localLoadSaveHelper.ageRangeBucketNumber,
//                mapper
//            )
//        }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ notifications: PaginatedScanList<BabbleTip> ->
//                deleteOldTips()
//                saveNotifications(notifications)
//                startDownload()
//            }) { throwable: Throwable ->
//                throwable.printStackTrace()
//                errorHasOccured()
//            }
//        disposables.add(disposable)
//    }


    fun retrieveNotifications(babyAge: Int, mapper: DynamoDBMapper): PaginatedScanList<BabbleTip> {
        val scanExpression = DynamoDBScanExpression()
        scanExpression.limit = 2000
        val attributeValue = HashMap<String, AttributeValue>()
        val age = Integer.toString(babyAge)
        val falseAttributeValue = AttributeValue()
        falseAttributeValue.s = "false"
        val ageAttributeValue = AttributeValue()
        ageAttributeValue.n = age
        attributeValue[":val"] = ageAttributeValue
        attributeValue[":val2"] = falseAttributeValue
        scanExpression.expressionAttributeValues = attributeValue
        scanExpression.filterExpression = "StartMonth<=:val AND EndMonth>=:val AND Deleted=:val2"
        return mapper.scan(BabbleTip::class.java, scanExpression)

    }

    private fun deleteOldTips() {
        if (context.filesDir.listFiles() != null) {
            val files: Array<out File>? = context.filesDir.listFiles()
            if (files != null) {
                for (child in files) {
                    if (child.name.contains("20")) {
                        child.delete()
                    }
                }
            }
        }
    }

    private fun saveNotifications(notifications: PaginatedScanList<BabbleTip>) {
        for (i in notifications.indices) {
            val tip = notifications[i]
            tip.id = i
        }
    }

    private fun addNotificationsFilesToList(notifications: List<BabbleTip>) {
        for (notification in notifications) {
            if (notification.soundFileName != "no file") {
                addFileToArray(notification.created, notification.soundFileName)
            }
            if (notification.videoFileName != "no file") {
                addFileToArray(notification.created, notification.videoFileName)
            }
        }
        totalCount = filesToDownloads.size
        if (canDownload()) {
            downloadFiles(filesdownloaded) {

            }
        } else {
           downloadFiles(0){

           }
        }
    }

    private fun canDownload(): Boolean {
        return totalCount != 0 && filesToDownloads.isNotEmpty()
    }

    private fun addFileToArray(dateCreated: String, name: String) {
        val fileName = "$dateCreated-$name"
        filesToDownloads.add(fileName)
    }


    fun downloadFiles(indexOfNextDownloadFile: Int, callback: (DownloadWorkerState) -> Unit) : Result {
        val fileName: String = filesToDownloads[indexOfNextDownloadFile]
        val file = File(context.filesDir, fileName)
        val observer: TransferObserver = transferUtility.download(
            BUCKET_NAME,  /* The bucket to download from */
            fileName,  /* The key for the object to download */
            file /* The file to download the object to */
        )
        observer.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (state == TransferState.COMPLETED) {
                    //downloadPresneterInterface.updateProgress(getProgress())
                    if (downloadHasNotCompleted()) {
                        filesdownloaded++
                        downloadFiles(filesdownloaded) {

                        }
                        callback.invoke(DownloadWorkerState.Progress(getProgress()))
                    } else {

                        callback.invoke(DownloadWorkerState.Done)
                       // downloadPresneterInterface.downloadCompleted()
                    }
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
               // callback.invoke(DownloadWorkerState.Progress())
            }
            override fun onError(id: Int, ex: Exception) {
                ex.printStackTrace()
                filesdownloaded++
                downloadFiles(filesdownloaded) {

                }
                if (ex is AmazonS3Exception) {
                    if (ex.statusCode != 404 || ex.statusCode!= 416) {
                        callback.invoke(DownloadWorkerState.Error)
                    }
                }
            }
        })
        return Result.success()
    }

    private fun getProgress(): Int {
        val progress: Double = filesdownloaded.toDouble() / totalCount.toDouble()
        return (progress * 100).toInt()
    }

    private fun downloadHasNotCompleted(): Boolean {
        return filesdownloaded != totalCount - 1
    }

    private suspend fun startForegroundService() {
        setForeground(
            ForegroundInfo(
                nextInt(),
                NotificationCompat.Builder(context, "download_channel")
                    .setSmallIcon(R.drawable.bwb_icon)
                    .setContentText("Downloading...")
                    .setContentTitle("Download in progress")
                    .build()
            )
        )
    }


}

sealed class DownloadWorkerState{
    data class Progress(val progress:Int):DownloadWorkerState()
    object Done : DownloadWorkerState()
    object Error : DownloadWorkerState()
}