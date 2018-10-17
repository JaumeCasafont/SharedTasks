package com.jcr.sharedtasks.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.jcr.sharedtasks.R;


public class TasksListWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String tasks) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tasks_list_widget);

        views.setTextViewText(R.id.tasks_widget_text, tasks);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        TasksListWidgetService.Companion.startActionUpdateIngredientsList(context);
    }

    public static void updateIngredientsListWidgets(Context context, AppWidgetManager appWidgetManager,
                                                    int[] appWidgetIds, String ingredients) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, ingredients);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first ingredients_list_widget_preview is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last ingredients_list_widget_preview is disabled
    }
}

