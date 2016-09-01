package net.roocky.mojian.AppWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import net.roocky.mojian.APPWidgetConfigure.ItemAppWidgetConfigure;
import net.roocky.mojian.Activity.ViewActivity;
import net.roocky.mojian.R;

/**
 * Created by Roocky on 2016/8/20 0020.
 * 小部件（展示单篇笔记）
 */
public class ItemProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i ++) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_item);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_item);
        remoteViews.setTextViewText(R.id.tv_content, intent.getStringExtra("content"));
    }
}