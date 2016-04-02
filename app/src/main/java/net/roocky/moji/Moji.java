package net.roocky.moji;

import android.app.Application;
import android.support.v4.util.ArrayMap;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;

import java.util.Map;

/**
 * Created by roocky on 04/02.
 *
 */
public class Moji extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FeedbackAPI.initAnnoy(this, getString(R.string.baichuan_app_key));
        Map<String, String> fbSetting = new ArrayMap<>();
        fbSetting.put("enableAudio", "0");  //关闭语音
        fbSetting.put("toAvatar", "http://xroocky.github.io/avatar.png");
        fbSetting.put("bgColor", "#9e9e9e");
        fbSetting.put("themeColor", "#9e9e9e");
        FeedbackAPI. setUICustomInfo(fbSetting);
    }
}
