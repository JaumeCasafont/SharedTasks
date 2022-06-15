package com.jcr.sharedtasks.ui

import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*

import com.google.firebase.auth.FirebaseUser
import com.jcr.sharedtasks.model.Project
import com.jcr.sharedtasks.model.ProjectReference
import com.jcr.sharedtasks.model.Task
import com.jcr.sharedtasks.repository.ProjectsRepository
import com.jcr.sharedtasks.repository.SignInRepository
import com.jcr.sharedtasks.util.AbsentLiveData
import com.jcr.sharedtasks.util.DeepLinkUtils
import kotlinx.coroutines.flow.*

import java.util.ArrayList
import java.util.UUID

import javax.inject.Inject

class MainActivityViewModel @Inject
constructor(private val signInRepository: SignInRepository, private val projectsRepository: ProjectsRepository) : ViewModel() {

    @VisibleForTesting
    private val logged = MutableStateFlow<Boolean?>(null)
    val projectUUIDs: StateFlow<List<ProjectReference>> = logged.transform { logged ->
        if (logged == true) {
            emitAll(projectsRepository.projectsReferences)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

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
