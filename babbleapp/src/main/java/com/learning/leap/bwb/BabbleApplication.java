package com.learning.leap.bwb;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.learning.leap.bwb.room.BabbleDatabase;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;


public class BabbleApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        BabbleDatabase.Companion.getInstance(this);
        Sentry.init("https://304314b3ecda47fe9439012d7bfbcd61@sentry.io/2888418", new AndroidSentryClientFactory(this));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
