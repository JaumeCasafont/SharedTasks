/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jcr.sharedtasks.binding

import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView

import com.jcr.sharedtasks.R
import com.jcr.sharedtasks.util.TimeUtils

import javax.inject.Inject

/**
 * Binding adapters that work with a fragment instance.
 */
class FragmentBindingAdapters @Inject
constructor(internal val fragment: Fragment) {

    @BindingAdapter("dateInDetail")
    fun bindDateInDetail(textView: TextView, timeInMillis: Long) {
        if (timeInMillis == 0L) {
            textView.text = fragment.getString(R.string.task_date_empty)
        } else {
            textView.text = TimeUtils.getDateFormatted(timeInMillis)
        }
    }

    @BindingAdapter("dateInList")
    fun bindDateInList(textView: TextView, timeInMillis: Long) {
        if (timeInMillis == 0L) {
            textView.visibility = View.INVISIBLE
        } else {
            textView.text = TimeUtils.getDateFormatted(timeInMillis)
        }
    }
}
