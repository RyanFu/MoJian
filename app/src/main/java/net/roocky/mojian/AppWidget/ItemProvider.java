package net.roocky.mojian.AppWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import net.roocky.mojian.APPWidgetConfigure.ItemAppWidgetConfigure;
import net.roocky.mojian.Activity.ViewActivity;
import net.roocky.mojian.Const;
import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;

/**
 * Created by Roocky on 2016/8/20 0020.
 * 小部件（展示单篇笔记）
 */
public class ItemProvider extends AppWidgetProvider {
    public static final String ACTION_EDIT = "roocky.intent.action.EDIT";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_EDIT)) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_item);
            remoteViews.setTextViewText(R.id.tv_content, intent.getStringExtra("content"));
            remoteViews.setInt(R.id.ll_content, "setBackgroundColor", Mojian.colors[intent.getIntExtra("paper", 0)]);
            remoteViews.setImageViewResource(R.id.iv_bottom, Mojian.backgrounds[intent.getIntExtra("background", 0)]);
            if (!intent.getStringExtra("remind").equals("")) {
                remoteViews.setViewVisibility(R.id.tv_remind, View.VISIBLE);
                remoteViews.setTextViewText(R.id.tv_remind, intent.getStringExtra("remind"));
            } else {
                remoteViews.setViewVisibility(R.id.tv_remind, View.GONE);
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(intent.getIntExtra("appwidget_id", Const.invalidId), remoteViews);
        }
    }
}