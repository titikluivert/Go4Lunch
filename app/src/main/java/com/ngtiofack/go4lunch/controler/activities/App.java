package com.ngtiofack.go4lunch.controler.activities;

import android.app.Application;

import com.evernote.android.job.JobManager;
import com.ngtiofack.go4lunch.utils.ReminderJobCreator;

/**
 * Created by NG-TIOFACK on 11/28/2018.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        JobManager.create(this).addJobCreator(new ReminderJobCreator());
    }
}
