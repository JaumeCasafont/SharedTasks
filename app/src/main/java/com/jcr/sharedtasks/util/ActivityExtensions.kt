package com.jcr.sharedtasks.util

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

inline fun <T> FragmentActivity.collectOnStarted(
    flow: Flow<T>,
    crossinline action: suspend (value: T) -> Unit
): Job {
    return lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect {
                action.invoke(it)
            }
        }
    }
}
