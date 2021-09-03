package com.learning.leap.bwb.download;


public interface DownloadViewInterface {
    void downloadCompleted();

    void displayErrorMessage();

    boolean comingFromAgeRange();

    void updateProgressBar(int progress);

}
