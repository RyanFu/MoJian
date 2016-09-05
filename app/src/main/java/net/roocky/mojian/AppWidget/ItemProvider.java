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
import net.roocky.mojian.Util.MapUtil;
import net.roocky.mojian.Util.SharePreferencesUtil;

import java.util.Map;

/**
 * Created by Roocky on 2016/8/20 0020.
 * 小部件（展示单篇笔记）
 */
public class ItemProvider extends AppWidgetProvider {
    public static final String ACTION_EDIT = "roocky.intent.action.EDIT";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(ACTION_EDIT) &&
                intent.getIntExtra("appwidget_id", Const.invalidId) != Const.invalidId) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_item);
            remoteViews.setTextViewText(R.id.tv_content, intent.getStringExtra("content"));
            remoteViews.setInt(R.id.ll_main, "setBackgroundColor", Mojian.colors[intent.getIntExtra("paper", 0)]);
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

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        //查找appWidgetId对应的ID
        Map<String, Integer> idMap = (Map<String, Integer>) SharePreferencesUtil.getInstance(context, Const.appWidgetIdShareP).getAll();
        if (MapUtil.getKeyByValue(idMap, appWidgetIds[0]) != null) {
            int id = Integer.parseInt(MapUtil.getKeyByValue(idMap, appWidgetIds[0]));
            //从SharePreference中移除该appwidget的id
            SharePreferencesUtil.getInstance(context, Const.appWidgetIdShareP).remove(String.valueOf(id));
        }
    }
}