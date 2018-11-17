package com.jcr.sharedtasks.repository

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.jcr.sharedtasks.api.ApiClient
import com.jcr.sharedtasks.db.ProjectsDao
import com.jcr.sharedtasks.model.Project
import com.jcr.sharedtasks.model.ProjectReference
import com.jcr.sharedtasks.model.Task
import com.jcr.sharedtasks.util.InstantAppExecutors
import com.jcr.sharedtasks.util.TestUtil
import com.jcr.sharedtasks.util.capture
import com.jcr.sharedtasks.util.mock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

@RunWith(JUnit4::class)
class ProjectsRepositoryTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private var projectsDao = Mockito.mock(ProjectsDao::class.java)
    private var sharedPreferences = Mockito.mock(SharedPreferences::class.java)
    private var apiClient = Mockito.mock(ApiClient::class.java)

    private var taskCaptor = ArgumentCaptor.forClass(Task::class.java) as ArgumentCaptor<Task>

    private lateinit var projectsRepository: ProjectsRepository

    @Before
    fun init() {
        `when`(sharedPreferences.getString("userUid", "")).thenReturn("userUid")
        `when`(sharedPreferences.getString("userName", " ")).thenReturn("userName")
        val editor = Mockito.mock(SharedPreferences.Editor::class.java)
        `when`(editor.putString("lastLoadedProject", "projectUUID")).thenReturn(editor)
        `when`(sharedPreferences.edit()).thenReturn(editor)

        val dbData = MutableLiveData<ProjectReference>()
        `when`(projectsDao.loadProjectReferenceById("projectUUID")).thenReturn(dbData)

        projectsRepository = ProjectsRepository(InstantAppExecutors(), projectsDao, sharedPreferences, apiClient)
    }

    @Test
    fun createProjectReferenceTest() {
        val projectReference = ProjectReference()
        projectsRepository.createProjectReference(projectReference)
        verify(apiClient).postValue("userUid", projectReference)
    }

    @Test
    fun createProjectTest() {
        val project = Project("projectUUID", "projectName")
        projectsRepository.createProject(project)

        verify(apiClient).postValue("projectUUID", project)
    }

    @Test
    fun getProjectReferencesTest() {
        val dbData = MutableLiveData<List<ProjectReference>>()
        `when`(projectsDao.loadProjectsReferences()).thenReturn(dbData)

        val projectReferences = MutableLiveData<ProjectReference>()
        `when`(apiClient.getProjectReferences("userUid")).thenReturn(projectReferences)

        val data = projectsRepository.projectsReferences
        data.observeForever(mock())

        verify(projectsDao).loadProjectsReferences()
        verify(apiClient).getProjectReferences("userUid")

        val projectReference = ProjectReference("projectUUID", "projectName")
        projectReferences.value = projectReference

        verify(projectsDao).insertProjectsReference(projectReference)
    }

    @Test
    fun getTasksTest() {
        val dbData = MutableLiveData<List<Task>>()
        `when`(projectsDao.loadTasks("projectUUID")).thenReturn(dbData)

        val projectData = MutableLiveData<Project>()
        `when`(apiClient.getProject("projectUUID")).thenReturn(projectData)

        val data = projectsRepository.loadTasks("projectUUID")
        data.observeForever(mock())

        verify(projectsDao).loadTasks("projectUUID")
        verify(apiClient).getProject("projectUUID")

        val project = Project("projectUUID", "projectName")
        val tasks = TestUtil.createTasks(10)
        project.tasks = tasks
        projectData.value = project

        verify(projectsDao).insertTasks(tasks)
    }

    @Test
    fun updateAssigneeTest() {
        val task = Task("taskProjectUUID", "taskSID", "taskName")
        task.assignee = "another user"
        task.taskProjectUUID = "projectUUID"
        task.remotePosition = 0
        projectsRepository.updateTaskAssignee(task)

        verify(projectsDao).insertTask(capture(taskCaptor))
        verify(apiClient).putValue("projectUUID/tasks/0", taskCaptor.value)
        assertEquals("userName", taskCaptor.value.assignee)
    }

    @Test
    fun updateTaskStatusTest() {
        val task = Task("taskProjectUUID", "taskSID", "taskName")
        task.state = 0
        task.taskProjectUUID = "projectUUID"
        task.remotePosition = 1
        task.isUploaded = true
        projectsRepository.updateTaskState(task)

        verify(projectsDao).insertTask(capture(taskCaptor))
        verify(apiClient).putValue("projectUUID/tasks/1", taskCaptor.value)
        assertEquals(1, taskCaptor.value.state)
    }
}