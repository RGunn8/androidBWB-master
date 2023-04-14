package com.learning.leap.bwb.download

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun DownloadScreen(){
    val context = LocalContext.current
    val request = OneTimeWorkRequestBuilder<DownloadWorker>()
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .build()

   val workManager =  WorkManager.getInstance(context)

    val workInfos = workManager
        .getWorkInfosForUniqueWorkLiveData("Progress")
        .observeAsState()
        .value

    val downloadInfo = remember(key1 = workInfos) {
        workInfos?.find { it.id == request.id }
    }

    val progress = downloadInfo?.progress

    workManager.enqueue(request)

    when(downloadInfo?.state) {
        WorkInfo.State.RUNNING -> Text("Downloading...")
        WorkInfo.State.SUCCEEDED -> Text("Download succeeded")
        WorkInfo.State.FAILED -> Text("Download failed")
        WorkInfo.State.CANCELLED -> Text("Download cancelled")
        WorkInfo.State.ENQUEUED -> Text("Download enqueued")
        WorkInfo.State.BLOCKED -> Text("Download blocked")
    }

    progress?.let {
        val pp = it.getInt("Progress",0)
        Log.d("test",pp.toString())
    }
}