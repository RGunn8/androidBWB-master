package com.learning.leap.bwb;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.multidex.MultiDex;
import androidx.work.Configuration;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.learning.leap.bwb.room.BabbleDatabase;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
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
        AndroidThreeTen.init(this);
        try {
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());

            Log.i("MyAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", e);
        }

        Sentry.init("https://304314b3ecda47fe9439012d7bfbcd61@sentry.io/2888418", new AndroidSentryClientFactory(this));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    "download_channel",
                    "File download",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
