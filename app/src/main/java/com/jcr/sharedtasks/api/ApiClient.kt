package com.jcr.sharedtasks.api

import androidx.lifecycle.LiveData
import com.google.firebase.database.*
import com.jcr.sharedtasks.model.Project
import com.jcr.sharedtasks.model.ProjectReference
import com.jcr.sharedtasks.model.Task
import com.jcr.sharedtasks.testing.OpenForTesting
import com.jcr.sharedtasks.util.FirebaseChildQueryLiveData
import com.jcr.sharedtasks.util.FirebaseQueryLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@OpenForTesting
class ApiClient@Inject constructor(private val dataRef: DatabaseReference) {

    fun postValue(reference: String, any: Any) {
        dataRef.child(reference).push().setValue(any)
    }

    fun putValue(reference: String, any: Any) {
        dataRef.child(reference).setValue(any)
    }

    fun getProjectReferences(userUid: String) : Flow<ProjectReference> {
        return getCallbackFlow(userUid, ProjectReference::class.java)
    }

    fun getProject(projectUUID: String) : Flow<Project> {
        return getCallbackFlow(projectUUID, Project::class.java)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> getCallbackFlow(pathString: String, dataType: Class<T>): Flow<T> {
        return callbackFlow {
            val postListener = object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.getValue(dataType)?.let {
                        this@callbackFlow.trySend(it)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.getValue(dataType)?.let {
                        this@callbackFlow.trySend(it)
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            }
            dataRef.addChildEventListener(postListener)

            awaitClose {
                dataRef.child(pathString).removeEventListener(postListener)
            }
        }
    }
}