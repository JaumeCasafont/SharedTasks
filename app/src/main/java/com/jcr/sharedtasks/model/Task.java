package com.jcr.sharedtasks.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Task {
    @PrimaryKey
    @NonNull
    public String taskSID;
    public String date;
    public String title;
    public boolean isAssigned;
    public String assignee;
    public String description;
    public int state;
    public boolean hasPriority;


    public Task(String taskSID) {
        this.taskSID = taskSID;
    }

    @Ignore
    public Task() {
    }

    @NonNull
    public String getTaskSID() {
        return taskSID;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getDescription() {
        return description;
    }

    public int getState() {
        return state;
    }

    public boolean isHasPriority() {
        return hasPriority;
    }
}
