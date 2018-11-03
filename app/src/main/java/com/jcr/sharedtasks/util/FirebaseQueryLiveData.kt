package com.jcr.sharedtasks.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import android.os.Handler
import androidx.annotation.WorkerThread
import android.util.Log

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener


class FirebaseQueryLiveData(dbReference: DatabaseReference) : LiveData<DataSnapshot>() {

    private val query = dbReference
    private val valueListener = mValueEventListener()

    private var listenerRemovePending = false
    private val handler = Handler()
    private val removeListener = Runnable {
        query.removeEventListener(valueListener)
        listenerRemovePending = false
    }

    override fun onActive() {
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener)
        } else {
            query.addValueEventListener(valueListener)
        }
        listenerRemovePending = false
    }

    override fun onInactive() {
        // Listener removal is schedule on a two second delay

        handler.postDelayed(removeListener, 2000)
        listenerRemovePending = true
    }


    private inner class mValueEventListener : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            value = dataSnapshot
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(LOG_TAG, "Cannot listen to query $query", databaseError.toException())
        }
    }

    companion object {
        private val LOG_TAG = "FirebaseQueryLiveData"
    }
}
