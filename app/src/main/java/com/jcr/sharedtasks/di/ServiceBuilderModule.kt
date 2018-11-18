package com.jcr.sharedtasks.di

import com.jcr.sharedtasks.sync.SyncDataFirebaseJobService
import com.jcr.sharedtasks.widget.TasksListWidgetService

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeTasksListWidgetService(): TasksListWidgetService

    @ContributesAndroidInjector
    abstract fun contributeSyncDataFirebaseJobService(): SyncDataFirebaseJobService
}
