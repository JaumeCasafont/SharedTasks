package com.jcr.sharedtasks.repository

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseUser
import com.jcr.sharedtasks.testing.OpenForTesting
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenForTesting
class SignInRepository @Inject
constructor(private val sharedPreferences: SharedPreferences) {

    fun onSignedInitialize(user: FirebaseUser) {
        sharedPreferences.edit().putString("userUid", user.uid).apply()
        sharedPreferences.edit().putString("userName", user.displayName).apply()
    }
}
