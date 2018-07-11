package com.jcr.sharedtasks.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.jcr.sharedtasks.model.Project;
import com.jcr.sharedtasks.repository.ProjectsRepository;
import com.jcr.sharedtasks.util.AbsentLiveData;

import javax.inject.Inject;

public class TasksListViewModel extends ViewModel {

    @VisibleForTesting
    final MutableLiveData<String> projectUUID;
    private final LiveData<Project> project;
    private final ProjectsRepository repository;

    @Inject
    public TasksListViewModel(ProjectsRepository repository) {
        this.projectUUID = new MutableLiveData<>();
        this.repository = repository;
        project = Transformations.switchMap(projectUUID, input -> {
            if (input.isEmpty()) {
                return AbsentLiveData.create();
            }
            return repository.loadProject(input);
        });
    }

    public void setProjectUUID(String projectUUID) {
        this.projectUUID.setValue(projectUUID);
    }

    public LiveData<Project> getProject() {
        return project;
    }
}
