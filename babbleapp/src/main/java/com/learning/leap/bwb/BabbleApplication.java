package com.learning.leap.bwb;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.multidex.MultiDex;
import androidx.work.Configuration;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

@HiltAndroidApp
public class BabbleApplication extends Application implements Configuration.Provider {

    @Inject HiltWorkerFactory workerFactory;

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Sentry.init("https://304314b3ecda47fe9439012d7bfbcd61@sentry.io/2888418", new AndroidSentryClientFactory(this));
        NotificationChannel channel = new NotificationChannel(
                "download_channel",
                "File download",
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
