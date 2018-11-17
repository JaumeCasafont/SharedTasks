package com.jcr.sharedtasks.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import android.net.Uri
import androidx.annotation.VisibleForTesting

import com.google.firebase.auth.FirebaseUser
import com.jcr.sharedtasks.model.Project
import com.jcr.sharedtasks.model.ProjectReference
import com.jcr.sharedtasks.model.Task
import com.jcr.sharedtasks.repository.ProjectsRepository
import com.jcr.sharedtasks.repository.SignInRepository
import com.jcr.sharedtasks.util.AbsentLiveData
import com.jcr.sharedtasks.util.DeepLinkUtils

import java.util.ArrayList
import java.util.UUID

import javax.inject.Inject

class MainActivityViewModel @Inject
constructor(private val signInRepository: SignInRepository, private val projectsRepository: ProjectsRepository) : ViewModel() {

    @VisibleForTesting
    private val logged = MutableLiveData<Boolean>()
    val projectUUIDs: LiveData<List<ProjectReference>> = Transformations
            .switchMap(logged) { isLogged ->
                if (isLogged) {
                    projectsRepository.projectsReferences
                } else {
                    AbsentLiveData.create()
                }
            }

    val isUserLogged: Boolean
        get() = logged.value != null && logged.value!!

    fun onSignedInitialize(user: FirebaseUser) {
        signInRepository.onSignedInitialize(user)
        logged.value = true
    }

    fun parseDeeplink(data: Uri): String {
        val invitedProjectReference = DeepLinkUtils.parseProjectUUID(data)
        projectsRepository.createProjectReference(invitedProjectReference)
        return invitedProjectReference.projectUUID
    }
}
