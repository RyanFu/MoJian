package net.roocky.mojian.Util;

import android.graphics.Bitmap;
import android.os.Environment;

import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by roocky on 04/17.
 * Bitmap保存工具类
 */
public class BitmapUtil {
    public static boolean save(Bitmap bitmap) {
        File directory = new File(Environment.getExternalStorageDirectory(),
                Mojian.getContext().getString(R.string.app_name_eng));
        if (!directory.exists()) {
            directory.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
