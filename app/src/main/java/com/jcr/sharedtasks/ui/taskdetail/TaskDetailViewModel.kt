package com.jcr.sharedtasks.ui.taskdetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
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
            return if (currentTask == null || currentTask.getDate() == 0L) {
                TimeUtils.getNow()
            } else {
                TimeUtils.getDateToCalendar(currentTask.getDate())
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
            taskToUpload = Task(UUID.randomUUID().toString())
        } else {
            this.taskSID.setValue(taskSID)
        }
    }

    fun getTaskToUpload(): Task? {
        if (taskToUpload == null) {
            taskToUpload = Task(task.value!!)
        }
        priority = taskToUpload!!.hasPriority()
        assignee = taskToUpload?.getAssignee()
        return taskToUpload
    }

    fun updateAssignee(assignee: String): String? {
        if (this.assignee == null) {
            this.assignee = assignee
        } else {
            this.assignee = if (this.assignee != assignee) assignee else null
        }
        taskToUpload?.setAssignee(this.assignee)
        return this.assignee
    }

    fun updatePriority(): Boolean {
        priority = !priority
        taskToUpload?.setHasPriority(priority)
        return priority
    }

    fun updateDate(date: Long) {
        taskToUpload?.setDate(date)
    }

    fun updateTitle(title: String) {
        taskToUpload?.setTitle(title)
    }

    fun updateDescription(description: String) {
        taskToUpload?.setDescription(description)
    }

    fun saveTask(title: String, description: String) {
        taskToUpload?.setTitle(title)
        taskToUpload?.setDescription(description)
        repository.sendTask(taskToUpload)
    }
}
