package com.learning.leap.bwb;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by ryangunn on 12/28/16.
 */

public class PlayTodayJobCreator implements JobCreator {


    @Override
    public Job create(String tag) {
        switch (tag) {
            case PlayTodayJob.PLAY_TODAY:
                return new PlayTodayJob();
            default:
                return null;
        }

    }
}
