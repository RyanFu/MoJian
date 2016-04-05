package net.roocky.moji.Util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by roocky on 03/29.
 * Android 6.0权限检查工具类
 */
public class Permission {
    /**
     * 如果已拥有权限，返回true，否则返回false
     * @param fragment      check()方法是在fragment中被调用的，所以传参时直接传this即可
     * @param permission    需要检查的权限
     * @param REQUEST_CODE  请求码，用来标识所请求的权限
     * @return
     */
    public static boolean check(Fragment fragment, String permission, int REQUEST_CODE) {

        if (ContextCompat.checkSelfPermission(fragment.getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, noGrantedList.get(0))) {
//                //向用户解释为什么需要请求该权限
//            } else {
            fragment.requestPermissions(new String[]{permission}, REQUEST_CODE);        //请求权限
//            }
            return false;
        } else {
            return true;
        }
    }

    //在Activity中判断是否有权限
    public static boolean checkA(Activity activity, String permission, int REQUEST_CODE) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }
}
