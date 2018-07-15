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
import com.jcr.sharedtasks.util.TimeUtils;

import java.util.UUID;

import javax.inject.Inject;

public class TaskDetailViewModel extends ViewModel {

    @VisibleForTesting
    final MutableLiveData<String> taskSID;
    private final LiveData<Task> task;
    private final ProjectsRepository repository;
    private Task taskToUpload;
    private boolean isNewTask = false;
    private long dateInMillis;
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
        if (taskSID == null) {
            isNewTask = true;
            taskToUpload = new Task(UUID.randomUUID().toString());
        } else {
            this.taskSID.setValue(taskSID);
        }
    }

    public void initValues() {
        taskToUpload = new Task(task.getValue());
        priority = taskToUpload.hasPriority();
        assignee = taskToUpload.getAssignee();
        dateInMillis = taskToUpload.getDate();
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
        return this.assignee;
    }

    public boolean updatePriority() {
        priority = !priority;
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
        dateInMillis = date;
    }

    public void saveTask(String title, String description) {
        taskToUpload.setTitle(title);
        taskToUpload.setDescription(description);
        taskToUpload.setDate(dateInMillis);
        taskToUpload.setHasPriority(priority);
        taskToUpload.setAssignee(assignee);
        repository.sendTask(taskToUpload);
    }
}
