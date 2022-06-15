package com.jcr.sharedtasks.sync

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.jcr.sharedtasks.model.Task
import com.jcr.sharedtasks.repository.ProjectsRepository

import javax.inject.Inject

import dagger.android.AndroidInjection

class SyncDataFirebaseJobService : JobService() {

    @Inject
    lateinit var repository: ProjectsRepository

    override fun onStartJob(job: JobParameters): Boolean {
        AndroidInjection.inject(this)

        repository.loadLocalTasks().observeForever { tasks ->
            if (tasks != null && !tasks.isEmpty()) {
                for (task in tasks) {
                    //repository.sendTask(task) // TODO migrate to work manager
                }
            }
        }

        jobFinished(job, true)
        return false
    }

    override fun onStopJob(job: JobParameters): Boolean {
        return false
    }
}
