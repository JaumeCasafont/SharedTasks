package com.jcr.sharedtasks.sync;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.jcr.sharedtasks.model.Task;
import com.jcr.sharedtasks.repository.ProjectsRepository;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class SyncDataFirebaseJobService extends JobService {

    @Inject
    ProjectsRepository repository;

    @Override
    public boolean onStartJob(JobParameters job) {
        AndroidInjection.inject(this);

        repository.loadLocalTasks().observeForever(tasks -> {
            if (tasks != null && !tasks.isEmpty()) {
                for (Task task : tasks) {
                    repository.sendTask(task);
                }
            }
        });

        jobFinished(job, true);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
