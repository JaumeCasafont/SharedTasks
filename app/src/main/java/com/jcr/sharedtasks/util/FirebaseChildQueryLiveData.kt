package com.jcr.sharedtasks.util

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

class FirebaseChildQueryLiveData(dbReference: DatabaseReference) : LiveData<DataSnapshot>() {

    private val query = dbReference
    private val childListener = MyEventListener()

    private var listenerRemovePending = false
    private val handler = Handler()
    private val removeListener = Runnable {
        query.removeEventListener(childListener)
        listenerRemovePending = false
    }

    override fun onActive() {
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener)
        } else {
            query.addChildEventListener(childListener)
        }
        listenerRemovePending = false
    }

    override fun onInactive() {
        handler.postDelayed(removeListener, 2000)
        listenerRemovePending = true
    }

    private inner class MyEventListener : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            value = dataSnapshot
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(LOG_TAG, "Cannot listen to query $query", databaseError.toException())
        }
    }

    companion object {
        private val LOG_TAG = "FirebaseQueryLiveData"
    }
}
