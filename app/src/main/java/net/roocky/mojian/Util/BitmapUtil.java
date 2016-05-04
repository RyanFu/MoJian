package net.roocky.mojian.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by roocky on 04/17.
 * Bitmap保存工具类
 */
public class BitmapUtil {
    /**
     * #description     将bitmap保存至本地
     * @param bitmap
     * @param path      保存路径（SD卡）
     * @param name      文件名
     * @return          返回值为0表明没有成功保存
     */
    public static long save(Bitmap bitmap, String path, String name) {
        File directory = new File(Environment.getExternalStorageDirectory()
                + path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        long currentTimeMill = System.currentTimeMillis();
        String fileName = name + currentTimeMill + ".jpg";
        File file = new File(directory, fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
            return currentTimeMill;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long save(Bitmap bitmap, String path) {
        return save(bitmap, path, "");
    }

    /**
     * #description         按屏幕尺寸进行压缩Bitmap
     * @param context
     * @param inputStream   图片输入流
     * @return
     */
    public static Bitmap compress(Context context, InputStream inputStream) {
        byte[] bytes = streamToBytes(inputStream);
        if (bytes == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;                          //只去读图片的头信息,不去解析真实的位图
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);     //获取图片信息
        int screenWidth = ScreenUtil.getWidth(context) - ScreenUtil.dip2px(context, 20);     //屏幕宽度
        int picWidth = options.outWidth;            //图片宽度
        int picHeight = options.outHeight;          //图片高度
        double scale = picWidth / (double)screenWidth;     //缩放比例
        options.inSampleSize = (int)Math.floor(scale);     //设置缩放比例(会自动向下取2的某次幂)
        options.inJustDecodeBounds = false;         //真正的去解析位图
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        return Bitmap.createScaledBitmap(               //二次缩小尺寸
                bitmap,
                (int)Math.floor(picWidth / scale),
                (int)Math.floor(picHeight / scale),
                false);
    }

    public static byte[] streamToBytes(InputStream inputStream) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte buffer[] = new byte[1024];
            while ((inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer);
            }
            byteArrayOutputStream.flush();
            inputStream.close();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
