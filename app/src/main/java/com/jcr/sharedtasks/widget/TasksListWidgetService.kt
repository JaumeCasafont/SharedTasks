package com.jcr.sharedtasks.widget

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.jcr.sharedtasks.R
import com.jcr.sharedtasks.repository.ProjectsRepository
import dagger.android.AndroidInjection
import javax.inject.Inject

class TasksListWidgetService : IntentService("IngredientsListService") {

    @Inject
    lateinit var mRepository: ProjectsRepository

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService()
        }

        AndroidInjection.inject(this)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startForegroundService() {
        val CHANNEL_ID = "my_channel_01"
        val channel = NotificationChannel(CHANNEL_ID,
                resources.getString(R.string.appwidget_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT)

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build()

        startForeground(1, notification)
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (action != null) {
                when (action) {
                    ACTION_UPDATE -> handleActionUpdateIngredientsListWidget()
                    else -> {
                    }
                }//ignore
            }
        }
    }

    private fun handleActionUpdateIngredientsListWidget() {
        Handler(mainLooper).post {
            mRepository.loadMyTasks().observeForever { tasks ->
                if (tasks != null) {
                    val appWidgetManager = AppWidgetManager.getInstance(this@TasksListWidgetService)

                    val appWidgetIds = appWidgetManager.getAppWidgetIds(
                            ComponentName(
                                    this@TasksListWidgetService,
                                    TasksListWidgetProvider::class.java))

                    val stringBuilder = StringBuilder()
                    for (task in tasks) {
                        stringBuilder.append("· ").append(task.title).append("\n")
                    }
                    TasksListWidgetProvider.updateIngredientsListWidgets(
                            this@TasksListWidgetService,
                            appWidgetManager, appWidgetIds, stringBuilder.toString())
                }
            }
        }
    }

    companion object {

        val ACTION_UPDATE = "com.jcr.sharedtasks.ingredients_list_widget_preview.extra.UPDATE"

        fun startActionUpdateIngredientsList(context: Context?) {
            val intent = Intent(context, TasksListWidgetService::class.java)
            intent.action = ACTION_UPDATE
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                context?.startService(intent)
            } else {
                context?.startForegroundService(intent)
            }
        }
    }
}
