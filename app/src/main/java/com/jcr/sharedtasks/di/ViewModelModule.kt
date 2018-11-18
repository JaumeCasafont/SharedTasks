package com.jcr.sharedtasks.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.jcr.sharedtasks.ui.MainActivityViewModel
import com.jcr.sharedtasks.ui.createproject.CreateProjectViewModel
import com.jcr.sharedtasks.ui.list.TasksListViewModel
import com.jcr.sharedtasks.ui.taskdetail.TaskDetailViewModel
import com.jcr.sharedtasks.viewmodel.SharedTasksViewModelFactory

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModel(mainActivityViewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TasksListViewModel::class)
    abstract fun bindTasksListViewModel(tasksListViewModel: TasksListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TaskDetailViewModel::class)
    abstract fun bindTaskDetailViewModel(taskDetailViewModel: TaskDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateProjectViewModel::class)
    abstract fun bindCreateProjectViewModel(createProjectViewModel: CreateProjectViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: SharedTasksViewModelFactory): ViewModelProvider.Factory
}
