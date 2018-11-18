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

package com.jcr.sharedtasks.di

import android.app.Application
import androidx.room.Room
import android.content.Context
import android.content.SharedPreferences

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jcr.sharedtasks.api.ApiClient
import com.jcr.sharedtasks.db.ProjectsDao
import com.jcr.sharedtasks.db.SharedTasksDb

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module(includes = [ViewModelModule::class])
internal class AppModule {

    @Singleton
    @Provides
    fun provideDb(app: Application): SharedTasksDb {
        return Room.databaseBuilder(app, SharedTasksDb::class.java, "tasks.db").build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: SharedTasksDb): ProjectsDao {
        return db.tasksDao()
    }

    @Singleton
    @Provides
    fun providePreferences(app: Application): SharedPreferences {
        return app.getSharedPreferences("com.jcr.sharedtasks", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideApiClient(): ApiClient {
        return ApiClient(provideFirebaseDatabase())
    }

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
    }
}
