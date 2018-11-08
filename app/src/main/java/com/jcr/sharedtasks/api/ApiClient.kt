package com.jcr.sharedtasks.api

import androidx.lifecycle.LiveData
import com.google.firebase.database.DatabaseReference
import com.jcr.sharedtasks.model.Project
import com.jcr.sharedtasks.model.ProjectReference
import com.jcr.sharedtasks.model.Task
import com.jcr.sharedtasks.testing.OpenForTesting
import com.jcr.sharedtasks.util.FirebaseChildQueryLiveData
import com.jcr.sharedtasks.util.FirebaseQueryLiveData
import javax.inject.Inject

@OpenForTesting
class ApiClient@Inject constructor(private val dataRef: DatabaseReference) {

    fun postValue(reference: String, any: Any) {
        dataRef.child(reference).push().setValue(any)
    }

    fun putValue(reference: String, any: Any) {
        dataRef.child(reference).setValue(any)
    }

    fun getProjectReferences(userUid: String) : LiveData<ProjectReference> {
        return FirebaseChildQueryLiveData(dataRef.child(userUid), ProjectReference::class.java)
                as LiveData<ProjectReference>
    }

    fun getProject(projectUUID: String) : LiveData<Project> {
        return FirebaseQueryLiveData(dataRef.child(projectUUID), Project::class.java)
                as LiveData<Project>
    }
}