package com.jcr.sharedtasks.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(indices = {@Index("projectUUID")})
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
