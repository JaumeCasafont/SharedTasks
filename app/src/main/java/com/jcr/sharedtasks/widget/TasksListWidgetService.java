package com.jcr.sharedtasks.widget;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.jcr.sharedtasks.R;
import com.jcr.sharedtasks.model.Task;
import com.jcr.sharedtasks.repository.ProjectsRepository;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class TasksListWidgetService extends IntentService {

    public static final String ACTION_UPDATE = "com.jcr.sharedtasks.ingredients_list_widget_preview.extra.UPDATE";

    @Inject
    ProjectsRepository mRepository;

    public TasksListWidgetService() {
        super("IngredientsListService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService();
        }

        AndroidInjection.inject(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startForegroundService() {
        String CHANNEL_ID = "my_channel_01";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                getResources().getString(R.string.appwidget_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build();

        startForeground(1, notification);
    }

    public static void startActionUpdateIngredientsList(Context context) {
        Intent intent = new Intent(context, TasksListWidgetService.class);
        intent.setAction(ACTION_UPDATE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(intent);
        } else {
            context.startForegroundService(intent);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_UPDATE:
                        handleActionUpdateIngredientsListWidget();
                        break;
                    default:
                        //ignore
                        break;
                }
            }
        }
    }

    private void handleActionUpdateIngredientsListWidget() {
            mRepository.loadMyTasks().observeForever(tasks -> {
                if (tasks != null) {
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TasksListWidgetProvider.class));
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Task task : tasks) {
                            stringBuilder.append("· ").append(task.getTitle()).append("\n");
                    }
                    TasksListWidgetProvider.updateIngredientsListWidgets(
                            this, appWidgetManager, appWidgetIds, stringBuilder.toString());
                }
            });
    }
}
