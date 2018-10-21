package com.jcr.sharedtasks.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer

import com.jcr.sharedtasks.model.Task
import com.jcr.sharedtasks.repository.ProjectsRepository
import com.jcr.sharedtasks.util.mock

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions

@RunWith(JUnit4::class)
class TasksListViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mock(ProjectsRepository::class.java)
    private var tasksListViewModel = TasksListViewModel(repository)

    @Test
    fun givenAProjectUUIDThenViewModelLoadsTasks() {
        tasksListViewModel.tasks.observeForever(mock())
        tasksListViewModel.setProjectUUID("projectUUID")
        verify<ProjectsRepository>(repository).loadTasks("projectUUID")
    }

    @Test
    fun givenTheSameProjectUUIDThenNoCallsAreMade() {
        tasksListViewModel.tasks.observeForever(mock())
        tasksListViewModel.setProjectUUID("projectUUID")
        verify<ProjectsRepository>(repository).loadTasks("projectUUID")
        tasksListViewModel.setProjectUUID("projectUUID")
        verifyNoMoreInteractions(repository)
    }
}
