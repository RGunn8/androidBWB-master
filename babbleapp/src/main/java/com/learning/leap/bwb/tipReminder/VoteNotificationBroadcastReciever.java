package com.learning.leap.bwb.tipReminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.learning.leap.bwb.R;
import com.learning.leap.bwb.helper.LocalLoadSaveHelper;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.vote.VoteViewActivity;


public class VoteNotificationBroadcastReciever extends BroadcastReceiver {
    private static final String channelID = "ChannelID";

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = channelID;
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.GREEN);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{1000, 2000});
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private static Notification getNotification(String content, PendingIntent pendingIntent, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
        builder.setContentTitle(Constant.BWB);
        builder.setContentText(content);
        Uri path = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.squeak);
        builder.setSound(path);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.playtoday_icon);
        builder.setAutoCancel(true);
        builder.setChannelId(channelID);
        builder.setWhen(System.currentTimeMillis());
        return builder.build();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent voteIntent = new Intent(context, VoteViewActivity.class);
        int numOfTips = intent.getIntExtra("NumberOfTips", 1);
        voteIntent.putExtra("NumberOfTips", numOfTips);
        int id = intent.getIntExtra("id", 1);
        voteIntent.putExtra("BucketNumber", id);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(voteIntent);
        voteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        LocalLoadSaveHelper saveHelper = new LocalLoadSaveHelper(context);
        String babyName = saveHelper.getBabyName();
        Resources res = context.getResources();
        String text = String.format(res.getString(R.string.notification_message), babyName);
        if (Build.VERSION.SDK_INT >= 26) {
            createChannel(context);
        }
        Notification notification = getNotification(text, notificationPendingIntent, context);
        NotificationManagerCompat.from(context).notify(intent.getIntExtra("id", 1), notification);
    }
}
