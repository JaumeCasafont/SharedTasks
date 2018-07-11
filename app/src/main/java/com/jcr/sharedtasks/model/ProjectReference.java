package com.jcr.sharedtasks.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class ProjectReference {

    @PrimaryKey
    @NonNull
    public String projectUUID;
    public String projectName;

    @Ignore
    public ProjectReference(){
    }

    public ProjectReference(String projectUUID, String projectName) {
        this.projectUUID = projectUUID;
        this.projectName = projectName;
    }

    @NonNull
    public String getProjectUUID() {
        return projectUUID;
    }

    public void setProjectUUID(String projectUUID) {
        this.projectUUID = projectUUID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
