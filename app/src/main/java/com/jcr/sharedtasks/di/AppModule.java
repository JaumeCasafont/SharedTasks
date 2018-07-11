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

package com.jcr.sharedtasks.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;

import com.jcr.sharedtasks.db.ProjectsDao;
import com.jcr.sharedtasks.db.SharedTasksDb;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
class AppModule {

    @Singleton @Provides
    SharedTasksDb provideDb(Application app) {
        return Room.databaseBuilder(app, SharedTasksDb.class,"tasks.db").build();
    }

    @Singleton @Provides
    ProjectsDao provideUserDao(SharedTasksDb db) {
        return db.tasksDao();
    }

    @Singleton @Provides
    SharedPreferences providePreferences(Application app) {
        return app.getSharedPreferences("com.jcr.sharedtasks", Context.MODE_PRIVATE);
    }
}
