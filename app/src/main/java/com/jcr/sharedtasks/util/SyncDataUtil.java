package com.jcr.sharedtasks.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.jcr.sharedtasks.sync.SyncDataFirebaseJobService;

import java.util.concurrent.TimeUnit;

public class SyncDataUtil {

    private static final int SYNC_DATA_INTERVAL_MINUTES = 1;
    private static final int SYNC_DATA_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(SYNC_DATA_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_DATA_INTERVAL_SECONDS;

    private static final String SYNC_DATA_JOB_TAG = "sync_data_tag";

    private static boolean sInitialized;

    synchronized public static void startSyncData(@NonNull final Context context) {

        if (sInitialized) return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job constraintSyncDataJob = dispatcher.newJobBuilder()
                .setService(SyncDataFirebaseJobService.class)
                .setTag(SYNC_DATA_JOB_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_DATA_INTERVAL_SECONDS,
                        SYNC_DATA_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))

                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(constraintSyncDataJob);

        sInitialized = true;
    }

}
