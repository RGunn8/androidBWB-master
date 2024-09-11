package com.learning.leap.bwb.localNotification

import android.Manifest
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.CallSuper
import androidx.core.app.ActivityCompat
import com.learning.leap.bwb.vote.VoteActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


abstract class HiltBroadcastReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context?, intent: Intent?) {
    }
}


@AndroidEntryPoint
class NotificationBroadcastReceiver : HiltBroadcastReceiver() {
    @Inject
    lateinit var notifier: Notifier
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val voteIntent = Intent(context, VoteActivity::class.java)
        intent?.let {
            val numberOfTips = it.getIntExtra(TipScheduler.NUM_OF_TIP, 1)
            val tipID = it.getIntExtra(TipScheduler.TIP_ID, 1)
            voteIntent.putExtra(TipScheduler.NUM_OF_TIP, numberOfTips)
            voteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addNextIntentWithParentStack(voteIntent)
            val notificationPendingIntent =
                stackBuilder.getPendingIntent(tipID, PendingIntent.FLAG_IMMUTABLE)
            context?.let { context: Context ->
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                notifier.showNotification(notificationPendingIntent, tipID)
            }

        }

    }
}