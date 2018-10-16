package com.jcr.sharedtasks.util

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.jcr.sharedtasks.TestApp

class SharedTasksTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestApp::class.java.name, context)
    }
}
