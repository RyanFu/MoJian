package net.roocky.mojian.Util;

import android.os.Build;

/**
 * Created by roock on 04/06.
 * 判断运行的系统版本是否大于等于所给版本
 */
public class SDKVersion {
    public static boolean isHigher(int VERSION_CODES) {
        if (Build.VERSION.SDK_INT > VERSION_CODES) {
            return true;
        } else {
            return false;
        }
    }
}
