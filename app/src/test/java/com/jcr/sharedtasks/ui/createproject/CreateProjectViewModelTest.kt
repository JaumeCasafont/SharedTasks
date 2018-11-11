package com.jcr.sharedtasks.ui.createproject

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jcr.sharedtasks.model.Project
import com.jcr.sharedtasks.repository.ProjectsRepository
import com.jcr.sharedtasks.util.capture
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentCaptor
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

        val projectCaptor = ArgumentCaptor.forClass(Project::class.java) as ArgumentCaptor<Project>

        verify(repository).createProject(capture(projectCaptor))
        assertEquals("projectName", projectCaptor.value.name)
    }

    @Test
    fun givenNoProjectNameThenNoCallsAreMade() {
        createProjectViewModel.createProject("")
        Mockito.verifyNoMoreInteractions(repository)
    }
}