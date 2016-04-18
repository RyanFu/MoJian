package net.roocky.mojian.Util;

import android.app.Activity;
import android.widget.Toast;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;

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
                //判断当前是否为自动更新
                if (!Mojian.isAutoUpdate) {         //手动更新
                    switch (updateStatus) {
                        case UpdateStatus.Yes: // has update
                            UmengUpdateAgent.showUpdateDialog(activity, updateInfo);
                            break;
                        case UpdateStatus.No: // has no update
                            Toast.makeText(activity, "「墨笺」已经是最新版本了~", Toast.LENGTH_SHORT).show();
                            break;
                        case UpdateStatus.NoneWifi: // none wifi
                            Toast.makeText(activity, "没有wifi连接，只在wifi下更新", Toast.LENGTH_SHORT).show();
                            break;
                        case UpdateStatus.Timeout: // time out
                            Toast.makeText(activity, "超时", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    Mojian.isAutoUpdate = true;     //恢复标识
                } else {        //自动更新
                    if (updateStatus == UpdateStatus.Yes) {
                        UmengUpdateAgent.showUpdateDialog(activity, updateInfo);
                    }
                }
            }
        });
    }
}
