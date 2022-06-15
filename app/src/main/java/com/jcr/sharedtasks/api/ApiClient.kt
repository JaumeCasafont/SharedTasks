package com.jcr.sharedtasks.api

import com.google.firebase.database.*
import com.jcr.sharedtasks.model.Project
import com.jcr.sharedtasks.model.ProjectReference
import com.jcr.sharedtasks.testing.OpenForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OpenForTesting
class ApiClient@Inject constructor(private val dataRef: DatabaseReference) {

    fun postValue(reference: String, any: Any) {
        dataRef.child(reference).push().setValue(any)
    }

    fun putValue(reference: String, any: Any) {
        dataRef.child(reference).setValue(any)
    }

    fun getProjectReferences(userUid: String) : Flow<List<ProjectReference>> {
        val projectReferences = mutableListOf<ProjectReference>()
        return getCallbackFlow(userUid, ProjectReference::class.java).map {
            projectReferences.add(it)
            projectReferences
        }
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
                    try {
                        snapshot.getValue(dataType)?.let {
                            this@callbackFlow.trySend(it)
                        }
                    } catch (e: DatabaseException) {
                        // can't convert data, don't send it
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    try {
                        snapshot.getValue(dataType)?.let {
                            this@callbackFlow.trySend(it)
                        }
                    } catch (e: DatabaseException) {
                        // can't convert data, don't send it
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            }
            dataRef.child(pathString).addChildEventListener(postListener)

            awaitClose {
                dataRef.child(pathString).removeEventListener(postListener)
            }
        }
    }
}