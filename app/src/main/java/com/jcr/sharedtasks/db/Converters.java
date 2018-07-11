package com.jcr.sharedtasks.db;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcr.sharedtasks.model.Task;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class Converters {

    static Gson gson = new Gson();

    @TypeConverter
    public static List<Task> stringToIngredientsList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Task>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ingredientsListToString(List<Task> tasks) {
        return gson.toJson(tasks);
    }
}
