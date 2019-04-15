package com.ngtiofack.go4lunch.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by NG-TIOFACK on 11/18/2018.
 */
public class ReminderJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {

        switch (tag) {
            case SyncJob.TAG:
                return new SyncJob();
            default:
                return null;
        }
    }
}
