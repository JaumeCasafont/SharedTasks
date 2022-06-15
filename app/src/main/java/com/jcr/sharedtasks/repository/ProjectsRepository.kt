package com.jcr.sharedtasks.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

import android.content.SharedPreferences
import androidx.lifecycle.Observer

import com.jcr.sharedtasks.AppExecutors
import com.jcr.sharedtasks.api.ApiClient
import com.jcr.sharedtasks.db.ProjectsDao
import com.jcr.sharedtasks.model.Project
import com.jcr.sharedtasks.model.ProjectReference
import com.jcr.sharedtasks.model.Task
import com.jcr.sharedtasks.testing.OpenForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

import java.util.ArrayList

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenForTesting
class ProjectsRepository @Inject
constructor(
    private val projectsDao: ProjectsDao,
    private val sharedPreferences: SharedPreferences,
    private val apiClient: ApiClient
) {

    private val projectReferencesCache: MutableList<ProjectReference>
    private var currentReference: ProjectReference? = null
    private var lastRemotePosition: Int = -1

    val currentProjectUUID: String
        get() = if (currentReference == null) "" else currentReference!!.projectUUID

    val currentProjectName: String
        get() = if (currentReference == null) "" else currentReference!!.projectName

    @OptIn(ExperimentalCoroutinesApi::class)
    val projectsReferences: Flow<List<ProjectReference>>
        get() {
            val userUid = sharedPreferences.getString("userUid", "")
            val dbSource = projectsDao.loadProjectsReferences()
            val networkSource =
                apiClient.getProjectReferences(userUid!!).onEach { projectReferences ->
                    projectsDao.insertProjectsReferences(projectReferences)
                }

//            result.addSource(dbSource) { result.setValue(it) }
//
//            result.addSource(networkSource) { dataSnapshot ->
//                val projectReference = saveInCache(dataSnapshot)
//                if (projectReference != null) {
//                    result.value = projectReferencesCache
//                    appExecutors.diskIO().execute {  }
//                }
//            }

            return merge(dbSource, networkSource).distinctUntilChanged()
        }

    init {

        projectReferencesCache = ArrayList()
    }

    fun createProjectReference(projectReference: ProjectReference) {
        val userUid = sharedPreferences.getString("userUid", "")
//        if (projectReferencePosition(projectReference) == -1) {
        apiClient.postValue(userUid!!, projectReference)
//        }
    }

    fun createProject(project: Project) {
        apiClient.postValue(project.projectUUID, project)

        val projectReference = ProjectReference(project.projectUUID, project.name)
        createProjectReference(projectReference)
    }

    fun getProjectReferenceById(projectUUID: String): Flow<ProjectReference> {
//        val result = MediatorLiveData<ProjectReference>()
//        result.addSource() { projectReference ->
//            currentReference = projectReference
//            result.setValue(currentReference)
//        }
        return projectsDao.loadProjectReferenceById(projectUUID)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadTasks(projectUUID: String): Flow<List<Task>> {
        sharedPreferences.edit().putString("lastLoadedProject", projectUUID).apply()
        val dbSource = projectsDao.loadTasks(projectUUID)
        val networkSource = apiClient.getProject(projectUUID).map { project ->
            project.tasks ?: emptyList()
        }.onEach {
            projectsDao.insertTasks(it)
        }

        return merge(dbSource, networkSource).distinctUntilChanged()
    }

    fun loadMyTasks(): LiveData<List<Task>> {
        return projectsDao.loadMyTasks(
            sharedPreferences.getString("userName", "")!!
        )
    }

    fun loadTask(taskSID: String): LiveData<Task> {
        return projectsDao.loadTask(taskSID)
    }

    suspend fun updateTaskState(task: Task) {
        val updatedStatusTask = Task(task)
        updatedStatusTask.state = updatedStatusTask.state + 1
        if (updatedStatusTask.state <= 2) {
            sendTask(updatedStatusTask)
        }
    }

    suspend fun updateTaskAssignee(task: Task) {
        val updatedAssigneeTask = Task(task)
        val assignee = sharedPreferences.getString("userName", " ")
        if (assignee != updatedAssigneeTask.assignee) {
            updatedAssigneeTask.assignee = assignee
            sendTask(updatedAssigneeTask)
        }
    }

    suspend fun sendTask(task: Task) {
        if (!task.isUploaded) {
            task.remotePosition = lastRemotePosition + 1
        }
        saveTask(task)
        uploadTask(task)
    }

    fun loadLocalTasks(): LiveData<List<Task>> {
        return projectsDao.loadLocalTasks()
    }

    private suspend fun saveTask(task: Task) {
        projectsDao.insertTask(task)
    }

    private fun uploadTask(task: Task) {
        apiClient.putValue(
            task.taskProjectUUID + "/tasks/" + task.remotePosition.toString(),
            task
        )
    }

//    private fun saveInCache(projectReference: ProjectReference?): ProjectReference? {
//        if (projectReference?.projectUUID != null) {
//            val position = projectReferencePosition(projectReference)
//            if (position == -1) {
//                projectReferencesCache.add(projectReference)
//            } else {
//                projectReferencesCache[position] = projectReference
//            }
//        }
//
//        return projectReference
//    }
//
//    private fun projectReferencePosition(projectReference: ProjectReference): Int {
//        if (projectReferencesCache.size == 0) return -1
//        for (i in projectReferencesCache.indices) {
//            if (projectReferencesCache[i].projectUUID == projectReference.projectUUID) {
//                return i
//            }
//        }
//        return -1
//    }

    private fun getProjectTasks(project: Project?): List<Task>? {
        if (project == null) return null
        syncTasksDataWithServer(project)
        return project.tasks
    }

    private fun syncTasksDataWithServer(project: Project) {
        if (project.tasks == null || project.tasks?.isEmpty()!!) {
            lastRemotePosition = -1
        } else {
            for (i in 0 until project.tasks?.size!!) {
                project.tasks?.get(i)!!.remotePosition = i
                project.tasks?.get(i)!!.isUploaded = true
                lastRemotePosition = i
            }
        }
    }
}
