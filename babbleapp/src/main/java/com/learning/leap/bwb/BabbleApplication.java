package com.learning.leap.bwb;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;


public class BabbleApplication extends Application {

    RealmMigration migration = (realm, oldVersion, newVersion) -> {
        ;
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0) {
            Set<String> fields = schema.get("BabblePlayer").getFieldNames();
            String babyGender = "babyGender";
            for (String field : fields) {
                if (field.equals(babyGender)) {
                    oldVersion++;
                    break;
                }
            }
            schema.get("BabblePlayer").addField("babyGender", String.class);
            oldVersion++;
        }

        if (oldVersion == 1) {
            Set<String> fields = schema.get("BabbleUser").getFieldNames();
            String groupCode = "groupCode";
            for (String field : fields) {
                if (field.equals(groupCode)) {
                    oldVersion++;
                    return;
                }
            }
            schema.get("BabbleUser").addField("groupCode", String.class);
            oldVersion++;
        }

        if (oldVersion == 2) {
            Set<String> fields = schema.get("BabbleUser").getFieldNames();
            String groupCode = "groupCode";
            for (String field : fields) {
                if (field.equals(groupCode)) {
                    return;
                }
            }
            schema.get("BabbleUser").addField("groupCode", String.class);

            Set<String> actionHistoryFields = schema.get("ActionHistory").getFieldNames();
            for (String field : actionHistoryFields) {
                if (field.equals(groupCode)) {
                    oldVersion++;
                    return;
                }
            }
            schema.get("ActionHistory").addField("groupCode", String.class);
            oldVersion++;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Sentry.init("https://304314b3ecda47fe9439012d7bfbcd61@sentry.io/2888418", new AndroidSentryClientFactory(this));
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(3)
                .migration(migration)
                .build();
        Realm.setDefaultConfiguration(config);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
