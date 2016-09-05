package net.roocky.mojian.AppWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import net.roocky.mojian.Activity.AddActivity;
import net.roocky.mojian.R;

/**
 * Created by roocky on 08/07.
 * 小部件（添加新便笺 or 日记）
 */
public class AddProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i ++) {
            //便笺Intent
            Intent intentNote = new Intent(context, AddActivity.class);
            intentNote.putExtra("from", "note");
            PendingIntent pendingIntentNote = PendingIntent.getActivity(context, 0, intentNote, 0);
            //日记Intent
            Intent intentDiary = new Intent(context, AddActivity.class);
            intentDiary.putExtra("from", "diary");
            PendingIntent pendingIntentDiary = PendingIntent.getActivity(context, 1, intentDiary, 0);
            //为RemoteViews绑定点击事件
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_add);
            remoteViews.setOnClickPendingIntent(R.id.btn_note, pendingIntentNote);
            remoteViews.setOnClickPendingIntent(R.id.btn_diary, pendingIntentDiary);
            //更新
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
    }
}
