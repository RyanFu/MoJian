package net.roocky.moji.Util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import net.roocky.moji.Activity.MainActivity;
import net.roocky.moji.R;

/**
 * Created by roocky on 04/01.
 * 处理友盟检查更新的结果工具类
 */
public class UmengUpdate {
    public static void set(final Activity activity) {
        UmengUpdateAgent.setUpdateAutoPopup(false);     //设置不自动弹出更新提示
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                //判断当前是否处于SettingFragment，R.id.ll_update为任意一个SettingFragment有而DiaryFragment没有的元素
                if (activity.findViewById(R.id.ll_update) != null) {
                    switch (updateStatus) {
                        case UpdateStatus.Yes: // has update
                            UmengUpdateAgent.showUpdateDialog(activity, updateInfo);
                            break;
                        case UpdateStatus.No: // has no update
                            Toast.makeText(activity, "「墨記」已经是最新版本了~", Toast.LENGTH_SHORT).show();
                            break;
                        case UpdateStatus.NoneWifi: // none wifi
                            Toast.makeText(activity, "没有wifi连接，只在wifi下更新", Toast.LENGTH_SHORT).show();
                            break;
                        case UpdateStatus.Timeout: // time out
                            Toast.makeText(activity, "超时", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    if (updateStatus == UpdateStatus.Yes) {
                        UmengUpdateAgent.showUpdateDialog(activity, updateInfo);
                    }
                }
            }
        });
    }
}
