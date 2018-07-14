package com.jcr.sharedtasks.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"taskSID", "taskProjectUUID"},
        foreignKeys = @ForeignKey(entity = ProjectReference.class,
                parentColumns = {"projectUUID"},
                childColumns = {"taskProjectUUID"},
                onUpdate = ForeignKey.CASCADE,
                deferred = true),
        indices = {@Index("taskProjectUUID")})
public class Task {
    @NonNull
    public String taskProjectUUID;
    @NonNull
    public String taskSID;
    public String date;
    public String title;
    public boolean isAssigned;
    public String assignee;
    public String description;
    public int state;
    public boolean hasPriority;
    public int remotePosition;

    public Task(String taskSID, String date, String title, boolean isAssigned, String assignee, String description, int state, boolean hasPriority) {
        this.taskSID = taskSID;
        this.date = date;
        this.title = title;
        this.isAssigned = isAssigned;
        this.assignee = assignee;
        this.description = description;
        this.state = state;
        this.hasPriority = hasPriority;
    }

    @Ignore
    public Task() {
    }

    @Ignore
    public Task(Task task) {
        this.taskProjectUUID = task.getTaskProjectUUID();
        this.taskSID = task.getTaskSID();
        this.date = task.getDate();
        this.title = task.getTitle();
        this.isAssigned = task.isAssigned();
        this.assignee = task.getAssignee();
        this.description = task.getDescription();
        this.state = task.getState();
        this.hasPriority = task.hasPriority();
    }

    @NonNull
    public String getTaskProjectUUID() {
        return taskProjectUUID;
    }

    public void setTaskProjectUUID(@NonNull String taskProjectUUID) {
        this.taskProjectUUID = taskProjectUUID;
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

    public boolean hasPriority() {
        return hasPriority;
    }

    public void setTaskSID(@NonNull String taskSID) {
        this.taskSID = taskSID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setHasPriority(boolean hasPriority) {
        this.hasPriority = hasPriority;
    }

    public int getRemotePosition() {
        return remotePosition;
    }

    public void setRemotePosition(int remotePosition) {
        this.remotePosition = remotePosition;
    }
}
