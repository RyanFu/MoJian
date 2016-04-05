package net.roocky.moji.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;

/**
 * Created by roocky on 04/05.
 * 显示反馈消息未读数的Notification被点击后发出一条广播，在此接收器中接收并打开反馈界面
 */
public class FeedbackReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FeedbackAPI.openFeedbackActivity(context);    //百川反馈
    }
}
