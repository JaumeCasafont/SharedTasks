package com.jcr.sharedtasks.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.support.annotation.VisibleForTesting;

import com.google.firebase.auth.FirebaseUser;
import com.jcr.sharedtasks.model.Project;
import com.jcr.sharedtasks.model.ProjectReference;
import com.jcr.sharedtasks.model.Task;
import com.jcr.sharedtasks.repository.ProjectsRepository;
import com.jcr.sharedtasks.repository.SignInRepository;
import com.jcr.sharedtasks.util.AbsentLiveData;
import com.jcr.sharedtasks.util.DeepLinkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

public class MainActivityViewModel extends ViewModel {

    @VisibleForTesting
    final MutableLiveData<Boolean> logged;
    private final LiveData<List<ProjectReference>> projectsReferences;
    private final SignInRepository signInRepository;
    private final ProjectsRepository projectsRepository;

    @Inject
    public MainActivityViewModel(SignInRepository signInRepository, ProjectsRepository projectsRepository) {
        this.logged = new MutableLiveData<>();
        this.signInRepository = signInRepository;
        this.projectsRepository = projectsRepository;
        projectsReferences = Transformations.switchMap(logged, logged -> {
            if (logged) {
                return projectsRepository.getProjectsReferences();
            } else {
                return AbsentLiveData.create();
            }
        });
    }

    public void onSignedInitialize(FirebaseUser user) {
        this.signInRepository.onSignedInitialize(user);
        logged.setValue(true);
    }

    public boolean isUserLogged() {
        return logged.getValue() != null && logged.getValue();
    }

    public LiveData<List<ProjectReference>> getProjectUUIDs() {
        return projectsReferences;
    }

    public String parseDeeplink(Uri data) {
        ProjectReference invitedProjectReference = DeepLinkUtils.parseProjectUUID(data);
        projectsRepository.createProjectReference(invitedProjectReference);
        return invitedProjectReference.getProjectUUID();
    }
}
