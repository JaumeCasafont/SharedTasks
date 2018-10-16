package com.jcr.sharedtasks.ui.taskdetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jcr.sharedtasks.repository.ProjectsRepository
import com.jcr.sharedtasks.util.mock
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class TaskDetailViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mock(ProjectsRepository::class.java)
    private var taskDetailViewModel = TaskDetailViewModel(repository)

    @Test
    fun givenATaskSIDThenLoadsIt() {
        taskDetailViewModel.task.observeForever(mock())
        taskDetailViewModel.setTaskSID("taskSID")
        verify<ProjectsRepository>(repository).loadTask("taskSID")
    }

    @Test
    fun givenTheSameTaskSIDThenNoCallsAreMade() {
        taskDetailViewModel.task.observeForever(mock())
        taskDetailViewModel.setTaskSID("taskSID")
        verify<ProjectsRepository>(repository).loadTask("taskSID")
        taskDetailViewModel.setTaskSID("taskSID")
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun givenATitleAndDescriptionViewModelCreatesTasksAndSendItToRepository() {
        taskDetailViewModel.setTaskSID(null)
        taskDetailViewModel.saveTask("title", "description")
        verify<ProjectsRepository>(repository).sendTask(taskDetailViewModel.getTaskToUpload())
    }

    @Test
    fun applyChangesToTaskThenTaskToUploadIsCorrect() {
        taskDetailViewModel.setTaskSID(null)
        taskDetailViewModel.updateTitle("title")
        taskDetailViewModel.updateDescription("description")
        taskDetailViewModel.updatePriority()
        taskDetailViewModel.updateDate(1000L)

        val taskToUpload = taskDetailViewModel.getTaskToUpload()
        assertThat(taskToUpload?.getTitle(), `is`("title"))
        assertThat(taskToUpload?.getDescription(), `is`("description"))
        assertThat(taskToUpload?.hasPriority(), `is`(true))
        assertThat(taskToUpload?.getDate(), `is`(1000L))
    }
}
