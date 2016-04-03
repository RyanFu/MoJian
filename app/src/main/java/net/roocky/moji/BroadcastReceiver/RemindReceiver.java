package net.roocky.moji.BroadcastReceiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.widget.TextView;

import net.roocky.moji.Activity.ViewActivity;
import net.roocky.moji.R;

/**
 * Created by roocky on 04/03.
 * 便笺提醒广播接收器。当到了所设定的时间时，系统会向该接收器发出一条广播，该接收器收到广播后发出一条通知
 */
public class RemindReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //创建Notification的跳转Intent
        Intent intentNotify = new Intent(context, ViewActivity.class);
        intentNotify.putExtra("from", "note");
        intentNotify.putExtra("id", intent.getStringExtra("id"));
        intentNotify.putExtra("content", intent.getStringExtra("content"));
        //创建模拟返回栈
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ViewActivity.class);
        stackBuilder.addNextIntent(intentNotify);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //创建Notification的Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText(intent.getStringExtra("content"));
        builder.setAutoCancel(true);
        //发出Notification
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }
}
