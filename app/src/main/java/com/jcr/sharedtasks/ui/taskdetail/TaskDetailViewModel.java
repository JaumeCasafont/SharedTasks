package com.jcr.sharedtasks.ui.taskdetail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.support.annotation.VisibleForTesting;

import com.jcr.sharedtasks.model.Task;
import com.jcr.sharedtasks.repository.ProjectsRepository;
import com.jcr.sharedtasks.util.AbsentLiveData;
import com.jcr.sharedtasks.util.Objects;
import com.jcr.sharedtasks.util.TimeUtils;

import java.util.UUID;

import javax.inject.Inject;

public class TaskDetailViewModel extends ViewModel {

    @VisibleForTesting
    final MutableLiveData<String> taskSID;
    private final LiveData<Task> task;
    private final ProjectsRepository repository;
    private Task taskToUpload;
    private boolean priority;
    private String assignee;

    @Inject
    public TaskDetailViewModel(ProjectsRepository repository) {
        this.taskSID = new MutableLiveData<>();
        this.repository = repository;
        task = Transformations.switchMap(taskSID, input -> {
            if (input.isEmpty()) {
                return AbsentLiveData.create();
            }
            return repository.loadTask(input);
        });
    }

    public void setTaskSID(String taskSID) {
        if (Objects.equals(taskSID, this.taskSID.getValue())) {
            return;
        }
        if (taskSID == null) {
            taskToUpload = new Task(UUID.randomUUID().toString());
        } else {
            this.taskSID.setValue(taskSID);
        }
    }

    public Task getTaskToUpload() {
        if (taskToUpload == null) {
            taskToUpload = new Task(task.getValue());
        }
        priority = taskToUpload.hasPriority();
        assignee = taskToUpload.getAssignee();
        return taskToUpload;
    }

    public LiveData<Task> getTask() {
        return task;
    }

    public String updateAssignee(String assignee) {
        if (this.assignee == null) {
            this.assignee = assignee;
        } else {
            this.assignee = !this.assignee.equals(assignee) ? assignee : null;
        }
        taskToUpload.setAssignee(this.assignee);
        return this.assignee;
    }

    public boolean updatePriority() {
        priority = !priority;
        taskToUpload.setHasPriority(priority);
        return priority;
    }

    public Integer[] getDateToShowInCalendar() {
        Task currentTask = task.getValue();
        if (currentTask == null || currentTask.getDate() == 0) {
            return TimeUtils.getNow();
        } else {
            return TimeUtils.getDateToCalendar(currentTask.getDate());
        }
    }

    public void updateDate(long date) {
        taskToUpload.setDate(date);
    }

    public void updateTitle(String title) {
        taskToUpload.setTitle(title);
    }

    public void updateDescription(String description) {
        taskToUpload.setDescription(description);
    }

    public void saveTask(String title, String description) {
        taskToUpload.setTitle(title);
        taskToUpload.setDescription(description);
        repository.sendTask(taskToUpload);
    }
}
