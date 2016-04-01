package com.umeng.fb.example;


import android.content.Context;
import android.util.Log;

import com.umeng.fb.push.FBMessage;
import com.umeng.fb.push.FeedbackPush;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

public class AppPush {

	private static String TAG = AppPush.class.getName();
	public UmengMessageHandler mMessageHandler;
    private static AppPush mAppPush;
    private Context context;

    public static AppPush getInstance(Context context) {
        if (mAppPush == null)
            mAppPush = new AppPush(context);
        return mAppPush;
    }

    public AppPush(Context context) {
        this.context = context;
    }

    public void init() {
        mMessageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(Context context, UMessage msg) {
                if (FeedbackPush.getInstance(context).dealFBMessage(new FBMessage(msg.custom))) {
                    //The push message is reply from developer.
                    return;
                }

                //The push message is not reply from developer.
                /*************** other code ***************/
				try {
					//可以通过MESSAGE_BODY取得消息体
					Log.d(TAG, "message=" + msg.getRaw().toString());    //消息体
					Log.d(TAG, "custom="+msg.custom);    //自定义消息的内容
					Log.d(TAG, "title="+msg.title);    //通知标题
					Log.d(TAG, "text="+msg.text);    //通知内容
					// code  to handle message here
					// ...
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}
            }
        };
        PushAgent.getInstance(context).setMessageHandler(mMessageHandler);
    }
}
