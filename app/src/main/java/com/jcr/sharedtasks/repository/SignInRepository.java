package com.jcr.sharedtasks.repository;

import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseUser;
import com.jcr.sharedtasks.AppExecutors;
import com.jcr.sharedtasks.db.ProjectsDao;
import com.jcr.sharedtasks.db.SharedTasksDb;

import javax.inject.Inject;

public class SignInRepository {

    private final SharedTasksDb db;

    private final ProjectsDao projectsDao;

    private final AppExecutors appExecutors;

    private final SharedPreferences sharedPreferences;

    @Inject
    public SignInRepository(AppExecutors appExecutors, SharedTasksDb db, ProjectsDao projectsDao,
                            SharedPreferences sharedPreferences) {
        this.db = db;
        this.projectsDao = projectsDao;
        this.appExecutors = appExecutors;
        this.sharedPreferences = sharedPreferences;
    }

    public void onSignedInitialize(FirebaseUser user) {
        sharedPreferences.edit().putString("userUid", user.getUid()).apply();
    }
}
