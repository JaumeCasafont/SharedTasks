package com.jcr.sharedtasks.ui.list

import androidx.lifecycle.*

import com.jcr.sharedtasks.model.ProjectReference
import com.jcr.sharedtasks.model.Task
import com.jcr.sharedtasks.repository.ProjectsRepository
import com.jcr.sharedtasks.testing.OpenForTesting
import com.jcr.sharedtasks.util.AbsentLiveData
import com.jcr.sharedtasks.util.DeepLinkUtils
import com.jcr.sharedtasks.util.Objects
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import javax.inject.Inject

@OpenForTesting
class TasksListViewModel @Inject
constructor(private val repository: ProjectsRepository) : ViewModel() {

    private val projectUUID = MutableStateFlow<String?>(null)

    val tasks: StateFlow<List<Task>> = projectUUID.transform { projectUUID ->
        projectUUID?.let {
            emitAll(repository.loadTasks(projectUUID))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val projectReference: StateFlow<ProjectReference?> = projectUUID.transform { projectUUID ->
        projectUUID?.let {
            emitAll(repository.getProjectReferenceById(projectUUID))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )


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
        viewModelScope.launch {
            repository.updateTaskState(task)
        }
    }

    fun updateTaskAssignee(task: Task) {
        viewModelScope.launch {
            repository.updateTaskAssignee(task)
        }
    }
}
