package com.learning.leap.bwb.models;

import android.content.Context;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.learning.leap.bwb.download.DownloadPresneterInterface;
import com.learning.leap.bwb.model.BabbleTip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AWSDownload {
    private static final String BUCKET_NAME = "leapbtob";
    private ArrayList<String> filesToDownloads = new ArrayList<>();
    private DownloadPresneterInterface downloadPresneterInterface;
    private TransferUtility mTransferUtility;
    private Context context;
    private int totalCount = 0;
    private int filesdownloaded = 0;

    public AWSDownload(Context context, TransferUtility transferUtility, DownloadPresneterInterface downloadPresneterInterface) {
        this.context = context;
        mTransferUtility = transferUtility;
        this.downloadPresneterInterface = downloadPresneterInterface;
    }

    public void addNotificationsFilesToList(List<BabbleTip> notifications) {
        for (BabbleTip notification : notifications) {
            if (!notification.getSoundFileName().equals("no file")) {
                addFileToArray(notification.getCreated(), notification.getSoundFileName());
            }

            if (!notification.getVideoFileName().equals("no file")) {
                addFileToArray(notification.getCreated(), notification.getVideoFileName());
            }
        }
        totalCount = filesToDownloads.size();
        if (canDownload()) {
            downloadFiles(filesdownloaded);
        } else {
            downloadPresneterInterface.errorHasOccured();
        }
    }

    private boolean canDownload() {
        return totalCount != 0 && !filesToDownloads.isEmpty();
    }

    private void addFileToArray(String dateCreated, String name) {
        String fileName = dateCreated + "-" + name;
        filesToDownloads.add(fileName);
    }


    public void downloadFiles(int indexOfNextDownloadFile) {
        String fileName = filesToDownloads.get(indexOfNextDownloadFile);
        File file = new File(context.getFilesDir(), fileName);
        TransferObserver observer = mTransferUtility.download(
                BUCKET_NAME,     /* The bucket to download from */
                fileName,    /* The key for the object to download */
                file        /* The file to download the object to */
        );
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    downloadPresneterInterface.updateProgress(getProgress());
                    if (downloadHasNotCompleted()) {
                        filesdownloaded++;
                        downloadFiles(filesdownloaded);

                    } else {
                        downloadPresneterInterface.downloadCompleted();
                    }
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                filesdownloaded++;
                downloadFiles(filesdownloaded);
                //downloadPresneterInterface.errorHasOccured();
            }
        });
    }

    private int getProgress() {
        double progress = (double) filesdownloaded / (double) totalCount;
        return (int) (progress * 100);
    }

    private Boolean downloadHasNotCompleted() {
        return filesdownloaded != totalCount - 1;
    }

    public int getFilesdownloaded() {
        return filesdownloaded;
    }

}
