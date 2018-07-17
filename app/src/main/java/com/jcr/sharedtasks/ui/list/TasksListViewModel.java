package com.jcr.sharedtasks.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.jcr.sharedtasks.model.ProjectReference;
import com.jcr.sharedtasks.model.Task;
import com.jcr.sharedtasks.repository.ProjectsRepository;
import com.jcr.sharedtasks.util.AbsentLiveData;
import com.jcr.sharedtasks.util.DeepLinkUtils;
import com.jcr.sharedtasks.util.Objects;

import java.util.List;

import javax.inject.Inject;

public class TasksListViewModel extends ViewModel {

    @VisibleForTesting
    final MutableLiveData<String> projectUUID;
    private final LiveData<List<Task>> tasks;
    private final ProjectsRepository repository;
    private final LiveData<ProjectReference> projectReference;

    @Inject
    public TasksListViewModel(ProjectsRepository repository) {
        this.projectUUID = new MutableLiveData<>();
        this.repository = repository;
        tasks = Transformations.switchMap(projectUUID, input -> {
            if (input.isEmpty()) {
                return AbsentLiveData.create();
            }
            return repository.loadTasks(input);
        });
        projectReference = Transformations.switchMap(projectUUID, input -> {
            if (input.isEmpty()) {
                return AbsentLiveData.create();
            }
            return repository.getProjectReferenceById(input);
        });
    }

    public String getDeepLinkOfCurrentProject() {
        return DeepLinkUtils.DEEPLINK_HOST + repository.getCurrentProjectName().replace(" ", "&")
                + "/" + repository.getCurrentProjectUUID();
    }

    public void setProjectUUID(String projectUUID) {
        if (Objects.equals(projectUUID, this.projectUUID.getValue())) {
            return;
        }
        this.projectUUID.setValue(projectUUID);
    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public LiveData<ProjectReference> getProjectReference() {
        return projectReference;
    }

    public void updateTaskStatus(Task task) {
        this.repository.updateTaskStatus(task);
    }

    public void updateTaskAssignee(Task task) {
        this.repository.updateTaskAssignee(task);
    }
}
