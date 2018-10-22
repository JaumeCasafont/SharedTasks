package com.jcr.sharedtasks.ui.createproject

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jcr.sharedtasks.model.Project
import com.jcr.sharedtasks.repository.ProjectsRepository
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.verify

@RunWith(JUnit4::class)
class CreateProjectViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = Mockito.mock(ProjectsRepository::class.java)
    private var createProjectViewModel = CreateProjectViewModel(repository)

    @Test
    fun givenAProjectNameThenCreateIt() {
        createProjectViewModel.createProject("projectName")
        verify<ProjectsRepository>(repository).createProject(any(Project::class.java))
    }

    @Test
    fun givenNoProjectNameThenNoCallsAreMade() {
        createProjectViewModel.createProject("")
        Mockito.verifyNoMoreInteractions(repository)
    }
}