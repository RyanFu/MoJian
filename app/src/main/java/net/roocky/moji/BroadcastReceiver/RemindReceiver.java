package net.roocky.moji.BroadcastReceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;

import net.roocky.moji.Activity.ViewActivity;
import net.roocky.moji.Database.DatabaseHelper;
import net.roocky.moji.R;

/**
 * Created by roocky on 04/03.
 * 便笺提醒广播接收器。当到了所设定的时间时，系统会向该接收器发出一条广播，该接收器收到广播后发出一条通知
 */
public class RemindReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int requestCode = Integer.parseInt(intent.getStringExtra("id"));
        //创建Notification的跳转Intent
        Intent intentNotify = new Intent(context, ViewActivity.class);
        intentNotify.putExtra("from", "note");
        intentNotify.putExtra("id", intent.getStringExtra("id"));
        intentNotify.putExtra("content", intent.getStringExtra("content"));
        intentNotify.putExtra("remind", "");    //此处的Intent仅用来显示一条便笺，并且该便笺的提醒时间已到
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                requestCode,
                intentNotify,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //创建Notification
        Notification notification = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.small_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(intent.getStringExtra("content"))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        //发出Notification
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(requestCode, notification);
        //将便笺的提醒时间设置为空
        DatabaseHelper databaseHelper;
        SQLiteDatabase database;
        databaseHelper = new DatabaseHelper(context, "Moji.db", null, 1);
        database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String string = null;
        values.put("remind", string);   //清空该便笺的remind
        database.update("note", values, "id = ?", new String[]{intent.getStringExtra("id")});
    }
}
