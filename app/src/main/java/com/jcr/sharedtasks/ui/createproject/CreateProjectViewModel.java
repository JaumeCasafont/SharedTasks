package com.jcr.sharedtasks.ui.createproject;

import android.arch.lifecycle.ViewModel;

import com.jcr.sharedtasks.model.Project;
import com.jcr.sharedtasks.repository.ProjectsRepository;

import java.util.UUID;

import javax.inject.Inject;

public class CreateProjectViewModel extends ViewModel {

    private final ProjectsRepository repository;

    @Inject
    public CreateProjectViewModel(ProjectsRepository projectsRepository) {
        this.repository = projectsRepository;
    }

    public String createProject(String projectName) {
        if (projectName.isEmpty()) return null;
        String projectUUID = UUID.randomUUID().toString();
        Project project = new Project(projectUUID, projectName);
        repository.createProject(project);
        return projectUUID;
    }
}
