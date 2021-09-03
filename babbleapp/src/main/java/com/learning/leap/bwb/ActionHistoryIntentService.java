package com.learning.leap.bwb;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.learning.leap.bwb.models.ActionHistory;
import com.learning.leap.bwb.research.ResearchActionHistory;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by ryanlgunn8 on 5/14/17.
 */

public class ActionHistoryIntentService extends IntentService {
    private final CompositeDisposable disposables = new CompositeDisposable();
    Context context;

    public ActionHistoryIntentService() {
        super("actionHistoryIntentService");
    }

    public static void startActionHistoryIntent(Context context) {
        Intent intent = new Intent(context, ActionHistoryIntentService.class);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        disposables.add(ActionHistory.uploadActionHistory(context));

    }
}
