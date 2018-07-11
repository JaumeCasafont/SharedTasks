package com.jcr.sharedtasks.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.Handler;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jcr.sharedtasks.AppExecutors;

import java.util.ArrayList;
import java.util.List;


public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {
    private static final String LOG_TAG = "FirebaseQueryLiveData";

    private final Query query;
    private final ValueEventListener valueListener = new mValueEventListener();
    private final ChildEventListener childListener = new MyEventListener();

    private boolean listenerRemovePending = false;
    private final Handler handler = new Handler();
    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            query.removeEventListener(valueListener);
            query.removeEventListener(childListener);
            listenerRemovePending = false;
        }
    };

    public FirebaseQueryLiveData(DatabaseReference dbReference){
        this.query = dbReference;
    }

    @Override
    protected void onActive() {
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener);
        }
        else {
            query.addValueEventListener(valueListener);
            query.addChildEventListener(childListener);
        }
        listenerRemovePending = false;
    }

    @Override
    protected void onInactive() {
        // Listener removal is schedule on a two second delay

        handler.postDelayed(removeListener, 2000);
        listenerRemovePending = true;
    }


    private class mValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                setValue(childSnapShot);
            }
//            appExecutors.diskIO().execute(() -> saveCallResult(dataSnapshot));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(LOG_TAG,  "Cannot listen to query " + query, databaseError.toException());
        }
    }

    private class MyEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            setValue(dataSnapshot);
//            appExecutors.diskIO().execute(() -> saveCallResult(dataSnapshot));
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(LOG_TAG, "Cannot listen to query " + query, databaseError.toException());
        }
    }

//    @WorkerThread
//    protected abstract void saveCallResult(DataSnapshot dataSnapshot);
}
