package com.jcr.sharedtasks.di;

import com.jcr.sharedtasks.widget.TasksListWidgetService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract TasksListWidgetService contributeTasksListWidgetService();
}
