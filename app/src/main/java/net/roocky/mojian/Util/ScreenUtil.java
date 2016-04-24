package net.roocky.mojian.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by roocky on 04/17.
 * 获取屏幕宽度&高度 & 几天
 */
public class ScreenUtil {

    //获取屏幕宽度
    public static int getWidth(Context context) {
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point.x;
    }

    //获取屏幕高度
    public static int getHeight(Context context) {
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point.y;
    }

    //将界面内容转为Bitmap
    public static Bitmap screenshot(View view, int width) {
        //测量&布局
        view.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        //绘制
        Bitmap bitmap = Bitmap.createBitmap(
                width,
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    /**
     * 判断软键盘是否显示
     * @param view  Activity根Layout
     * @return
     */
    public static boolean isSoftInputShow(View view) {
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        if (rect.bottom < getHeight(view.getContext())) {
            return true;
        } else {
            return false;
        }
    }
}
