package net.roocky.mojian;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by roocky on 04/02.
 *
 */

/**
 * preference：
 * ——————————————————
 * boolean:
 *      isFirst                 标识当前是否为首次启动APP
 * int:
 *      defaultPaper            下次新建时使用的纸张颜色
 *      defaultBackground       下次新建时使用的背景图片
 *      fontSize                文本字号大小
 *      drawerPosition          抽屉位置（左or右）
 *      launchFragment          下次启动APP时默认打开的Fragment（便笺or日记）
 * String:
 *      background0             便笺顶部背景图Uri
 *      background1             日记顶部背景图Uri
 *      background2             设置顶部背景图Uri
 *      avatar                  头像Uri
 *      nickname                昵称
 *      signature               个性签名
 *      patternSha1             日记图案锁的SHA1值
 *      tempNote                便笺草稿
 *      tempDiary               日记草稿
 */
public class Mojian extends Application {
    public static Context context;

    public static String[] numbers;
    //当前日期&时间
    public static int year;
    public static int month;
    public static int day;
    public static int hour;
    public static int minute;
    //RecyclerView刷新类型
    public static final int FLUSH_ADD = 0;
    public static final int FLUSH_REMOVE = 1;
    public static final int FLUSH_ALL = 2;
    //当前日记是否为锁定状态
    public static boolean isLocked = true;
    //字体大小
    public static float[] fontSize = {20, 17.5f, 15};
    /**
     * normal，StatusBar背景色 & Toolbar背景色 & 4.x版本CardView的内容背景 & ViewActivity纸张背景
     */
    public static int[] colors = {
            0xffFFFFFF,
            0xffFFEBEE,
            0xffFFFDE7,
            0xffEEEEEE,
            0xffE8F5E9,
            0xffE3F2FD
    };
    /**
     * dark，华为等机型的StatusBar背景色，CardView的日期背景
     */
    public static int[] darkColors = {//纸张dark颜色，用于CardView的日期背景
            0xffbdbdbd,
            0xffDFCBCE,
            0xffDFDDC7,
            0xff9e9e9e,
            0xffC8D5C9,
            0xffC3D2DD
    };
    /**
     * 不同纸张颜色对应的主题
     */
    public static int[] themeIds = {
            R.style.ActivityPaperA,
            R.style.ActivityPaperB,
            R.style.ActivityPaperC,
            R.style.ActivityPaperD,
            R.style.ActivityPaperE,
            R.style.ActivityPaperF
    };
    /**
     * 5.x版本CardView的内容背景
     */
    public static int[] ripples = {
            R.drawable.bg_ripple_paper_a,
            R.drawable.bg_ripple_paper_b,
            R.drawable.bg_ripple_paper_c,
            R.drawable.bg_ripple_paper_d,
            R.drawable.bg_ripple_paper_e,
            R.drawable.bg_ripple_paper_f
    };

    //背景图片
    public static int[] backgrounds = {
            R.drawable.bottom_null,
            R.drawable.bottom_mei,
            R.drawable.bottom_lan,
            R.drawable.bottom_he,
            R.drawable.bottom_zhu,
            R.drawable.bottom_ju
    };

    //需要处理的设备型号
    public static List<String> devices = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        numbers = getResources().getStringArray(R.array.number_array);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        devices = Arrays.asList(getResources().getStringArray(R.array.devices));

//        setBaichuanFb();
    }

    //百川反馈
//    private void setBaichuanFb() {
//        FeedbackAPI.initAnnoy(this, getString(R.string.baichuan_app_key));
//        Map<String, String> fbSetting = new ArrayMap<>();
//        fbSetting.put("enableAudio", "0");  //关闭语音
//        fbSetting.put("toAvatar", "http://xroocky.github.io/avatar.png");
//        fbSetting.put("bgColor", "#9e9e9e");
//        fbSetting.put("themeColor", "#9e9e9e");
//        FeedbackAPI. setUICustomInfo(fbSetting);
//    }

    //获取Context
    public static Context getContext() {
        return context;
    }

    //刷新时间
    public static void flushTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }
}
