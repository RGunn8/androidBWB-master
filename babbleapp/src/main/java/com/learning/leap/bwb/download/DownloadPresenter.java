package com.learning.leap.bwb.download;

import android.content.Context;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;
import com.learning.leap.bwb.helper.ScheduleBucket;
import com.learning.leap.bwb.models.AWSDownload;
import com.learning.leap.bwb.models.Notification;

import io.reactivex.disposables.Disposable;
import io.realm.Realm;


public class DownloadPresenter implements DownloadPresneterInterface {
    private Boolean paused = false;
    private Context context;
    private AWSDownload awsDownload;
    private int filesDownloadAtPaused = 0;
    private Disposable realmNotificationSubscription;

    public DownloadPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void errorHasOccured() {
        //downloadViewInterface.displayErrorMessage();
    }

    @Override
    public void downloadCompleted() {
        //downloadViewInterface.downloadCompleted();
    }

    @Override
    public void updateProgress(int prgress) {
        //downloadViewInterface.updateProgressBar(prgress);
    }

//    @Override
//    public void onCreate() {
//        AmazonS3 mAmazonS3 = new AmazonS3Client(Utility.getCredientail(context));
//        TransferUtility transferUtility = new TransferUtility(mAmazonS3, context.getApplicationContext());
//        awsDownload = new AWSDownload(context,transferUtility,this);
//        realmNotificationSubscription = new Notification().getNotificationFromRealm(Realm.getDefaultInstance())
//                                        .subscribe(notifications -> awsDownload.addNotificationsFilesToList(notifications),
//                                                   throwable -> errorHasOccured());
//    }

//    @Override
//    public void onDestory() {
//        if (realmNotificationSubscription != null && !realmNotificationSubscription.isDisposed()){
//            realmNotificationSubscription.dispose();
//        }
//    }
//
//    @Override
//    public void onResume() {
//        if (paused) {
//            awsDownload.downloadFiles(filesDownloadAtPaused);
//            paused = false;
//        }
//    }
//
//    @Override
//    public void onPaused() {
//        paused = true;
//        filesDownloadAtPaused(awsDownload.getFilesdownloaded());
//    }

    private void filesDownloadAtPaused(int filesDownloaded) {
        filesDownloadAtPaused = filesDownloaded;
    }
}
