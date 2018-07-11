package com.jcr.sharedtasks.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.jcr.sharedtasks.ui.MainActivityViewModel;
import com.jcr.sharedtasks.ui.list.TasksListViewModel;
import com.jcr.sharedtasks.viewmodel.SharedTasksViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel.class)
    abstract ViewModel bindMainActivityViewModel(MainActivityViewModel mainActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TasksListViewModel.class)
    abstract ViewModel bindTasksListViewModel(TasksListViewModel tasksListViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(SharedTasksViewModelFactory factory);
}
