package com.jcr.sharedtasks.ui.taskdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.jcr.sharedtasks.model.Task
import com.jcr.sharedtasks.repository.ProjectsRepository
import com.jcr.sharedtasks.testing.OpenForTesting
import com.jcr.sharedtasks.util.AbsentLiveData
import com.jcr.sharedtasks.util.Objects
import com.jcr.sharedtasks.util.TimeUtils
import java.util.*
import javax.inject.Inject

@OpenForTesting
class TaskDetailViewModel @Inject
constructor(private val repository: ProjectsRepository) : ViewModel() {

    private val taskSID = MutableLiveData<String>()
    private var taskToUpload: Task? = null
    private var priority: Boolean = false
    private var assignee: String? = null

    val dateToShowInCalendar: Array<Int>
        get() {
            val currentTask = task.value
            return if (currentTask == null || currentTask.date == 0L) {
                TimeUtils.now
            } else {
                TimeUtils.getDateToCalendar(currentTask.date)
            }
        }

    val task: LiveData<Task> = Transformations
            .switchMap(taskSID) { input ->
            if (input.isEmpty()) {
                AbsentLiveData.create()
            } else {
                repository.loadTask(input)
            }
        }

    fun setTaskSID(taskSID: String?) {
        if (Objects.equals(taskSID, this.taskSID.value) && taskSID != null) {
            return
        }
        if (taskSID == null) {
            taskToUpload = Task(repository.currentProjectUUID, UUID.randomUUID().toString())
        } else {
            this.taskSID.setValue(taskSID)
        }
    }

    fun getTaskToUpload(): Task {
        if (taskToUpload == null) {
            taskToUpload = Task(task.value!!)
        }
        priority = taskToUpload?.hasPriority!!
        assignee = taskToUpload?.assignee
        return taskToUpload!!
    }

    fun updateAssignee(assignee: String): String? {
        if (this.assignee == null) {
            this.assignee = assignee
        } else {
            this.assignee = if (this.assignee != assignee) assignee else null
        }
        taskToUpload?.assignee = this.assignee
        return this.assignee
    }

    fun updatePriority(): Boolean {
        priority = !priority
        taskToUpload?.hasPriority = priority
        return priority
    }

    fun updateDate(date: Long) {
        taskToUpload?.date = date
    }

    fun updateTitle(title: String) {
        taskToUpload?.title = title
    }

    fun updateDescription(description: String) {
        taskToUpload?.description = description
    }

    fun saveTask(title: String, description: String) {
        taskToUpload?.title = title
        taskToUpload?.description = description
        repository.sendTask(taskToUpload!!)
    }
}
