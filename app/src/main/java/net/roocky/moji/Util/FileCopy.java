package net.roocky.moji.Util;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by roocky on 10/17.
 * 拷贝文件工具类
 */
public class FileCopy {
    /**
     * 文件拷贝
     * @param src       从这里拷贝
     * @param target    拷贝到这里
     * @return
     */
    public static boolean copy(String src, String target) {
        try {
            FileInputStream inputStream = new FileInputStream(src);
            FileOutputStream outputStream = new FileOutputStream(target);
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
}
