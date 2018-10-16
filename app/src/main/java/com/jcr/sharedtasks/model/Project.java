package com.jcr.sharedtasks.model;

import androidx.annotation.NonNull;

import java.util.List;

public class Project {

    private String projectUUID;
    private List<Task> tasks;
    private String name;

    public Project(@NonNull String projectUUID, String name) {
        this.projectUUID = projectUUID;
        this.name = name;
    }

    public Project() {
    }

    @NonNull
    public String getProjectUUID() {
        return projectUUID;
    }

    public void setProjectUUID(@NonNull String projectUUID) {
        this.projectUUID = projectUUID;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
