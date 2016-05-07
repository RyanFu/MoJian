package net.roocky.mojian.Widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import android.util.Log;

import net.roocky.mojian.Mojian;
import net.roocky.mojian.Util.ScreenUtil;

/**
 * Created by roocky on 05/06.
 * 带有居中对齐的ImageSpan
 */
public class AlignImageSpan extends ImageSpan {
    public static final int ALIGN_CENTER = 2;

    public AlignImageSpan(Context context, Bitmap b, int verticalAlignment) {
        super(context, b, verticalAlignment);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text,
                     int start, int end, float x,
                     int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();

        int transY = bottom - b.getBounds().bottom;     //底部对齐
        switch (mVerticalAlignment) {
            case ALIGN_BASELINE:        //顶部对齐
                transY -= paint.getFontMetricsInt().descent;
                break;
            case ALIGN_CENTER:          //居中对齐
                transY -= paint.getFontMetricsInt().descent - paint.getFontMetricsInt().ascent / 2;
                break;
        }

        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}
