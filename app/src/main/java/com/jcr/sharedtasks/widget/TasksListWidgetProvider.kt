package com.jcr.sharedtasks.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

import com.jcr.sharedtasks.R


class TasksListWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        TasksListWidgetService.startActionUpdateIngredientsList(context)
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    companion object {

        private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int, tasks: String) {

            val views = RemoteViews(context.packageName, R.layout.tasks_list_widget)

            views.setTextViewText(R.id.tasks_widget_text, tasks)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateIngredientsListWidgets(context: Context, appWidgetManager: AppWidgetManager,
                                         appWidgetIds: IntArray, ingredients: String) {
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, ingredients)
            }
        }
    }
}

