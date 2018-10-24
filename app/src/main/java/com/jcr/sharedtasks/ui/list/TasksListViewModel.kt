package com.jcr.sharedtasks.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.annotation.VisibleForTesting

import com.jcr.sharedtasks.model.ProjectReference
import com.jcr.sharedtasks.model.Task
import com.jcr.sharedtasks.repository.ProjectsRepository
import com.jcr.sharedtasks.testing.OpenForTesting
import com.jcr.sharedtasks.util.AbsentLiveData
import com.jcr.sharedtasks.util.DeepLinkUtils
import com.jcr.sharedtasks.util.Objects

import javax.inject.Inject

@OpenForTesting
class TasksListViewModel @Inject
constructor(private val repository: ProjectsRepository) : ViewModel() {

    private val projectUUID = MutableLiveData<String>()

    val tasks: LiveData<List<Task>> = Transformations
            .switchMap(projectUUID) { input ->
                if (input.isEmpty()) {
                    AbsentLiveData.create()
                } else {
                    repository.loadTasks(input)
                }
            }

    val projectReference: LiveData<ProjectReference> = Transformations
            .switchMap(projectUUID) { input ->
                if (input.isEmpty()) {
                    AbsentLiveData.create()
                } else {
                    repository.getProjectReferenceById(input)
                }
            }

    val deepLinkOfCurrentProject: String
        get() = (DeepLinkUtils.DEEPLINK_HOST + repository.currentProjectName.replace(" ", "&")
                + "/" + repository.currentProjectUUID)

    fun setProjectUUID(projectUUID: String) {
        if (Objects.equals(projectUUID, this.projectUUID.value)) {
            return
        }
        this.projectUUID.value = projectUUID
    }

    fun updateTaskStatus(task: Task) {
        this.repository.updateTaskStatus(task)
    }

    fun updateTaskAssignee(task: Task) {
        this.repository.updateTaskAssignee(task)
    }
}
