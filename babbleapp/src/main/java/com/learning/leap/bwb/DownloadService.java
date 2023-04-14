package com.learning.leap.bwb;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.learning.leap.bwb.download.DownloadPresneterInterface;
import com.learning.leap.bwb.download.DownloadViewInterface;
import com.learning.leap.bwb.helper.LocalLoadSaveHelper;
import com.learning.leap.bwb.model.BabbleTip;
import com.learning.leap.bwb.model.BabbleUser;
import com.learning.leap.bwb.models.AWSDownload;
import com.learning.leap.bwb.room.BabbleDatabase;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class DownloadService extends Service implements DownloadPresneterInterface {
    private final IBinder mBinder = new LocalBinder();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Boolean started = false;
    private DownloadViewInterface downloadViewInterface;
    private AWSDownload awsDownload;
    private Disposable realmNotificationSubscription;
    private boolean update = false;

    public void registerClient(Context context) {
        downloadViewInterface = (DownloadViewInterface) context;
    }

    private boolean nullViewCheck() {
        return downloadViewInterface != null;
    }

    @Override
    public void errorHasOccured() {
        if (nullViewCheck()) {
            downloadViewInterface.displayErrorMessage();
        }
        stopSelf();
    }

    @Override
    public void downloadCompleted() {
        if (nullViewCheck()) {
            downloadViewInterface.downloadCompleted();
        }
        onComplete();
        stopSelf();
    }

    @Override
    public void updateProgress(int prgress) {
        if (nullViewCheck()) {
            downloadViewInterface.updateProgressBar(prgress);
        }
    }

    private void startDownload() {
//        if (!started) {
//            AmazonS3 mAmazonS3 = new AmazonS3Client(Utility.getCredientail(this));
//            TransferUtility transferUtility = new TransferUtility(mAmazonS3, this.getApplicationContext());
//            awsDownload = new AWSDownload(this, transferUtility, this);
//            realmNotificationSubscription = BabbleDatabase.Companion.getInstance(getApplicationContext()).babbleTipDAO().getAll()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(notifications -> {
//                                awsDownload.addNotificationsFilesToList(notifications);
//                                int filesDownloadAtPaused = 0;
//                                awsDownload.downloadFiles(filesDownloadAtPaused);
//                                started = true;
//                            },
//                            throwable -> errorHasOccured());
//            disposables.add(realmNotificationSubscription);
//        }
    }

    private void updateNotifications() {
        LocalLoadSaveHelper localLoadSaveHelper = new LocalLoadSaveHelper(this);
        Utility.writeBoolenSharedPreferences(Constant.UPDATE, true, this);
        BabbleUser babblePlayer = BabbleUser.Companion.loadBabblePlayerFromSharedPref(localLoadSaveHelper);
        AmazonDynamoDBClient amazonDynamoDBClient = new AmazonDynamoDBClient(Utility.getCredientail(this));
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
        Disposable disposable = Single.fromCallable(() -> babblePlayer.retrieveNotifications(localLoadSaveHelper.getAgeRangeBucketNumber(), mapper))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notifications -> {
                    deleteOldTips();
                    saveNotifications(notifications);
                    startDownload();
                }, throwable -> {
                    throwable.printStackTrace();
                    errorHasOccured();
                });

        disposables.add(disposable);
    }

    private void deleteOldTips() {
        if (this.getFilesDir().listFiles() != null) {
            File[] files = this.getFilesDir().listFiles();
            if (files != null) {
                for (File child : files) {
                    if (child.getName().contains("20")) {
                        child.delete();
                    }
                }
            }
        }

    }

    private void saveNotifications(PaginatedScanList<BabbleTip> notifications) {
        for (int i = 0; i < notifications.size(); i++) {
            BabbleTip tip = notifications.get(i);
            tip.setId(i);
        }
    }

    @Override
    public void onDestroy() {
        if (realmNotificationSubscription != null && !realmNotificationSubscription.isDisposed()) {
            realmNotificationSubscription.dispose();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra(Constant.UPDATE, false)) {
            updateNotifications();
            update = true;
        } else {
            startDownload();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void onComplete() {
        Utility.writeBoolenSharedPreferences(Constant.UPDATE, false, this);
        if (!downloadViewInterface.comingFromAgeRange()) {
            Utility.writeIntSharedPreferences(Constant.START_TIME, 8, this);
            Utility.writeIntSharedPreferences(Constant.END_TIME, 20, this);
            Utility.writeBoolenSharedPreferences(Constant.DID_DOWNLOAD, true, this);
            Utility.writeBoolenSharedPreferences(Constant.SEND_TIPS_TODAY, true, this);
            Utility.writeIntSharedPreferences(Constant.TIPS_PER_DAY, 3, this);
            Utility.writeBoolenSharedPreferences(Constant.TIP_ONE_ON, true, this);
            Utility.writeBoolenSharedPreferences(Constant.TIP_TWO_ON, true, this);
            Calendar dueDate = Calendar.getInstance();
            dueDate.add(Calendar.MINUTE, 1);
            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DailyWorker.class).
                    setInitialDelay(dueDate.getTimeInMillis(), TimeUnit.MILLISECONDS).build();
            WorkManager.getInstance(this).enqueue(request);
            setUpdDailyWorker();
            if (update) {
                BabbleUser.Companion.saveUpdatedInfo(this);
            }
        }

    }

    private void setUpdDailyWorker() {
        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();
        dueDate.set(Calendar.HOUR_OF_DAY, 1);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }
        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();
        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(DailyWorker.class, 1, TimeUnit.DAYS).setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .build();
        WorkManager.getInstance(this.getApplicationContext()).enqueue(periodicWork);
    }

    public class LocalBinder extends Binder {
        public DownloadService getServiceInstance() {
            return DownloadService.this;
        }
    }


}
