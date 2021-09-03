package com.learning.leap.bwb.download;

import com.learning.leap.bwb.baseInterface.LifeCycleInterface;

public interface DownloadPresneterInterface {
    void errorHasOccured();

    void downloadCompleted();

    void updateProgress(int prgress);
}
