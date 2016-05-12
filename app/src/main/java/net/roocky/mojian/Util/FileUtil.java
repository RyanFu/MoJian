package net.roocky.mojian.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by roocky on 10/17.
 * 拷贝文件工具类
 */
public class FileUtil {
    /**
     * 文件拷贝
     * @param src       从这里拷贝
     * @param target    拷贝到这里
     * @param name      拷贝得到的文件名
     * @return
     */
    public static boolean copy(String src, String target, String name) {
        try {
            FileInputStream inputStream = new FileInputStream(src);
            File dir = new File(target);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream outputStream = new FileOutputStream(new File(dir, name));
            byte buffer[] = new byte[1024];
            while ((inputStream.read(buffer)) != -1) {
                outputStream.write(buffer);
            }
            inputStream.close();
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copy(InputStream inputS, String target) {
        try {
            InputStream inputStream = inputS;
            FileOutputStream outputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while ((inputStream.read(buffer)) != -1) {
                outputStream.write(buffer);
            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
