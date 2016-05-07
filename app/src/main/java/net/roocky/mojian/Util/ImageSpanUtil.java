package net.roocky.mojian.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import net.roocky.mojian.Mojian;
import net.roocky.mojian.Widget.AlignImageSpan;

/**
 * Created by roocky on 05/06.
 * ImageSpan工具类
 */
public class ImageSpanUtil {

    /**
     * #description         将String转为SpannableStringBuilder
     * @param strContent
     * @return
     */
    public static SpannableStringBuilder str2spanStrBuilder(String strContent) {
        SpannableStringBuilder ssbContent = new SpannableStringBuilder();
        StringBuilder strUrl = new StringBuilder();
        boolean isUrl = false;
        for (int i = 0; i < strContent.length(); i ++) {
            if (strContent.charAt(i) == '<') {          //路径开始标志
                isUrl = true;
            } else if (strContent.charAt(i) == '>') {   //路径结束标志
                String tag = "<" + strUrl + ">";
                Bitmap bitmap = BitmapFactory.decodeFile(strUrl.toString());
                if (bitmap == null) {                   //该路径并不是一个真实的图片路径
                    ssbContent.append('<').append(strUrl).append('>');
                    strUrl.delete(0, strUrl.length());
                    isUrl = false;
                    continue;
                }
                AlignImageSpan imageSpan = new AlignImageSpan(Mojian.context, bitmap, AlignImageSpan.ALIGN_CENTER);
                SpannableString spannableString = new SpannableString(tag);
                spannableString.setSpan(imageSpan, 0, tag.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssbContent.append(spannableString);
                strUrl.delete(0, strUrl.length());
                isUrl = false;
            } else {
                if (isUrl){     //路径
                    strUrl.append(String.valueOf(strContent.charAt(i)));
                } else {        //普通文本
                    ssbContent.append(String.valueOf(strContent.charAt(i)));
                }
            }
        }
        return ssbContent;
    }

    /**
     * #description         从包含ImageSpan的字符串中去除ImageSpan
     * @param strContent
     * @return
     */
    public static StringBuilder getString(String strContent) {
        StringBuilder strResult = new StringBuilder();
        boolean isUrl = false;
        for (int i = 0; i < strContent.length(); i ++) {
            if (strContent.charAt(i) == '<') {
                isUrl = true;
                //若'<'前面是普通文本且没有'\n'，则需为其添加'\n'
                if (i > 1 && strContent.charAt(i - 1) != '>' && strContent.charAt(i - 1) != '\n') {
                    strResult.append('\n');
                }
            } else if (strContent.charAt(i) == '>') {
                isUrl = false;
                //若'>'后面为'\n'则需将'\n'移除，以保证图片之间没有'\n'
                if (i + 1 < strContent.length() && strContent.charAt(i + 1) == '\n') {
                    i ++;
                }
            } else if (!isUrl) {            //非“<>”内的字符
                strResult.append(strContent.charAt(i));
            }
        }
        return strResult;
    }
}
