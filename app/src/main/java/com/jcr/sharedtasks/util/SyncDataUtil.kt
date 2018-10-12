package com.jcr.sharedtasks.util

import android.content.Context

import com.firebase.jobdispatcher.Constraint
import com.firebase.jobdispatcher.Driver
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Job
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.Trigger
import com.jcr.sharedtasks.sync.SyncDataFirebaseJobService

import java.util.concurrent.TimeUnit

object SyncDataUtil {

    private val SYNC_DATA_INTERVAL_MINUTES = 1
    private val SYNC_DATA_INTERVAL_SECONDS = TimeUnit.MINUTES.toSeconds(SYNC_DATA_INTERVAL_MINUTES.toLong()).toInt()
    private val SYNC_FLEXTIME_SECONDS = SYNC_DATA_INTERVAL_SECONDS

    private val SYNC_DATA_JOB_TAG = "sync_data_tag"

    private var sInitialized: Boolean = false

    @Synchronized
    fun startSyncData(context: Context) {

        if (sInitialized) return
        val driver = GooglePlayDriver(context)
        val dispatcher = FirebaseJobDispatcher(driver)

        val constraintSyncDataJob = dispatcher.newJobBuilder()
                .setService(SyncDataFirebaseJobService::class.java)
                .setTag(SYNC_DATA_JOB_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_DATA_INTERVAL_SECONDS,
                        SYNC_DATA_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))

                .setReplaceCurrent(true)
                .build()

        dispatcher.schedule(constraintSyncDataJob)

        sInitialized = true
    }

    @Synchronized
    fun cancelSyncData(context: Context) {
        val driver = GooglePlayDriver(context)
        val dispatcher = FirebaseJobDispatcher(driver)

        dispatcher.cancel(SYNC_DATA_JOB_TAG)
    }
}
