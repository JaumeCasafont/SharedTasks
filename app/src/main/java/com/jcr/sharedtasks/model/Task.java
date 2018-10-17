package com.jcr.sharedtasks.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

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
    public long date;
    public String title;
    public String assignee;
    public String description;
    public int state;
    public boolean hasPriority;
    public int remotePosition;
    public boolean isUploaded;

    public Task(String taskSID, long date, String title, String assignee, String description, int state, boolean hasPriority, int remotePosition, boolean isUploaded) {
        this.taskSID = taskSID;
        this.date = date;
        this.title = title;
        this.assignee = assignee;
        this.description = description;
        this.state = state;
        this.hasPriority = hasPriority;
        this.remotePosition = remotePosition;
        this.isUploaded = isUploaded;
    }

    @Ignore
    public Task() {
    }

    @Ignore
    public Task(String taskSID) {
        this.taskSID = taskSID;
    }

    @Ignore
    public Task(Task task) {
        this.taskProjectUUID = task.getTaskProjectUUID();
        this.taskSID = task.getTaskSID();
        this.date = task.getDate();
        this.title = task.getTitle();
        this.assignee = task.getAssignee();
        this.description = task.getDescription();
        this.state = task.getState();
        this.hasPriority = task.hasPriority();
        this.remotePosition = task.getRemotePosition();
        this.isUploaded = task.isUploaded;
    }

    @Ignore
    public Task(String taskSID, String title) {
        this.taskSID = taskSID;
        this.title = title;
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

    public long getDate() {
        return date;
    }

    public String getTitle() {
        return title;
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

    public void setDate(long date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }
}
