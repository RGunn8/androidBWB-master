package com.learning.leap.bwb.localNotification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.learning.leap.bwb.R
import com.learning.leap.bwb.utility.Constant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class Notifier @Inject constructor(
    private val context: Context,
    private val dataStore: DataStore<Preferences>,
) {

    private val channelID = "Babble"

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(pendingIntent: PendingIntent, id: Int) {
        createChannel()
        val notification = getNotification(pendingIntent, context)
        notificationManager.notify(
            id,
            notification
        )
    }

    private fun createChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(channelID, channelID, importance)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern = longArrayOf(1000, 2000)
        notificationManager.createNotificationChannel(notificationChannel)

    }

    private fun getNotification(
        pendingIntent: PendingIntent,
        context: Context
    ): Notification {
        val builder =
            NotificationCompat.Builder(context, channelID)
        builder.setContentTitle(Constant.BWB)
        builder.setContentText(createNotificationContent(context))
        val path = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.squeak)
        builder.setSound(path)
        builder.setContentIntent(pendingIntent)
        builder.setSmallIcon(R.drawable.playtoday_icon)
        builder.setAutoCancel(true)
        builder.setChannelId(channelID)
        builder.setWhen(System.currentTimeMillis())
        return builder.build()
    }


    private fun createNotificationContent(context: Context): String {
        val babyName = runBlocking {
            dataStore.data.map {
                it[stringPreferencesKey(Constant.BABY_NAME)] ?: ""
            }.first()
        }
        val res = context.resources
        return String.format(res.getString(R.string.notification_message), babyName)
    }
}