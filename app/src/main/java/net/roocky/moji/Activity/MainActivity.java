package net.roocky.moji.Activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.IWxCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import net.roocky.moji.BroadcastReceiver.FeedbackReceiver;
import net.roocky.moji.Database.DatabaseHelper;
import net.roocky.moji.Fragment.BaseFragment;
import net.roocky.moji.Fragment.DiaryFragment;
import net.roocky.moji.Fragment.NoteFragment;
import net.roocky.moji.Fragment.SettingFragment;
import net.roocky.moji.Moji;
import net.roocky.moji.R;
import net.roocky.moji.Util.UmengUpdate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        IWxCallback,
        DialogInterface.OnClickListener {

    @Bind(R.id.iv_background)
    ImageView ivBackground;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.ctl_main)
    CollapsingToolbarLayout ctlMain;
    @Bind(R.id.fl_content)
    FrameLayout flContent;
    @Bind(R.id.fab_add)
    FloatingActionButton fabAdd;
    @Bind(R.id.cl_main)
    CoordinatorLayout clMain;

    private final int FRAGMENT_NOTE = 0;
    private final int FRAGMENT_DIARY = 1;
    private final int FRAGMENT_SETTING = 2;
    private final int ACTIVITY_VIEW = 0;
    private final int ACTIVITY_ADD = 1;
    private final int FLUSH_ADD = 0;
    private final int FLUSH_REMOVE = 1;
    private final int FLUSH_ALL = 2;

    private SlidingMenu slidingMenu;        //侧滑菜单
    private LinearLayout llAccount;         //信息展示卡片
    private SimpleDraweeView sdvAvatar;     //用户头像
    private TextView tvNickname;            //用户昵称
    private TextView tvSignature;           //个性签名

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private NoteFragment noteFragment = new NoteFragment();
    private DiaryFragment diaryFragment = new DiaryFragment();
    private SettingFragment settingFragment = new SettingFragment();

    private int fragmentId = FRAGMENT_NOTE;         //记录当前所在的Fragment
    private List<Fragment> fragmentList = new ArrayList<>();        //存放便笺、日记、设置三个Fragment
    private String[] ttMenus = {"便箋", "日記", "設置"};
    private int[] idMenus = {R.id.btn_note, R.id.btn_diary, R.id.btn_setting};      //便笺、日记、设置三项的ID
    private int[] bgToolbar = {R.drawable.bd_note, R.drawable.bd_diary, R.drawable.bd_setting}; //Toolbar背景图片

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        UmengUpdateAgent.setDeltaUpdate(false); //设置为全量更新
        UmengUpdate.set(this);                  //设置友盟检查更新结果的处理
        UmengUpdateAgent.update(this);          //友盟检查更新
        FeedbackAPI.getFeedbackUnreadCount(this, null, this);   //检查百川反馈未读消息

        initExplain();          //使用说明初始化
        setSlidingMenu();       //设置SlidingMenu
        initView();             //View初始化
        setOnClickListener();

        itemSelected(R.id.btn_note);   //设置默认“便笺”项被选中
    }

    //初始化使用说明
    private void initExplain() {
        preferences = getSharedPreferences("moji", MODE_PRIVATE);
        editor = preferences.edit();

        if (preferences.getBoolean("isFirst", true)) {
            editor.putBoolean("isFirst", false).apply();   //设置一个布尔变量标识是否第一次运行App

            databaseHelper = new DatabaseHelper(this, "Moji.db", null, 1);
            database = databaseHelper.getWritableDatabase();
            String[] numbers = getResources().getStringArray(R.array.number_array);

            //向数据库中存入使用说明
            ContentValues values = new ContentValues();
            values.put("year", Moji.year);
            values.put("month", Moji.month);
            values.put("day", Moji.day);
            values.put("content", getString(R.string.use_explain_note));
            database.insert("note", null, values);

            values.remove("content");
            values.put("content", getString(R.string.use_explain_diary));
            database.insert("diary", null, values);
        }
    }

    //初始化View
    private void initView() {
        llAccount = (LinearLayout)findViewById(R.id.ll_account);
        sdvAvatar = (SimpleDraweeView)findViewById(R.id.sdv_avatar);
        tvNickname = (TextView)findViewById(R.id.tv_nickname);
        tvSignature = (TextView)findViewById(R.id.tv_signature);

        setSupportActionBar(toolbar);
        
        fragmentManager.beginTransaction().replace(R.id.fl_content, noteFragment).commit();
        fragmentList.add(noteFragment);
        fragmentList.add(diaryFragment);
        fragmentList.add(settingFragment);
    }

    //设置侧滑抽屉菜单
    private void setSlidingMenu() {
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.SLIDING_WINDOW);      //菜单位置
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);    //滑动位置
        slidingMenu.setBehindWidth((int) (0.75f * getResources().getDisplayMetrics().widthPixels));  //菜单宽度
        slidingMenu.setFadeDegree(0.5f);        //淡入淡出
        slidingMenu.setBehindScrollScale(0f);   //菜单缩放
        slidingMenu.setShadowDrawable(R.drawable.shadow);       //阴影设置
        slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);     //设置此项可解决虚拟键遮挡问题
        slidingMenu.setMenu(R.layout.menu_slidingmenu);
    }

    //绑定控件点击事件
    private void setOnClickListener() {
        llAccount.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        //日记、便笺、设置三项
        for (int idMenu : idMenus) {
            findViewById(idMenu).setOnClickListener(this);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingMenu.isMenuShowing()) {
                    slidingMenu.showContent();
                } else {
                    slidingMenu.showMenu();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_account:
                startActivity(new Intent(this, AccountActivity.class));
                break;
            case R.id.fab_add:
                if (noteFragment.isDeleting || diaryFragment.isDeleting) {
                    new AlertDialog.Builder(this)
                            .setTitle("删除")
                            .setMessage("确定删除吗？")
                            .setPositiveButton("确定", this)
                            .setNegativeButton("取消", null)
                            .show();
                } else {
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    //新建日记和便笺的Activity为同一个，但是Intent携带参数不同
                    switch (fragmentId) {
                        case FRAGMENT_NOTE:
                            intent.putExtra("from", "note");
                            break;
                        case FRAGMENT_DIARY:
                            intent.putExtra("from", "diary");
                            break;
                    }
                    startActivity(intent);
                }
                break;
            default:
                itemSelected(v.getId());
                break;
        }
    }

    //当菜单子项被选中时进行的操作
    private void itemSelected(int id) {
        for (int i = 0; i < idMenus.length; i++) {
            /**
             * 如果取到的ID等于被点击的ID，则需将被点击的菜单颜色设置成黑色来凸显当前所选择的菜单，把Toolbar的title
             * 设置为相应的文字。再进行Fragment替换操作，然后把fragmentId设置为该ID以表示当前所在的Fragment，根据
             * 所在Fragment设置对应的Toolbar背景图片。如果所在Fragment为“设置”则不需要显示FloatActionBar
             */
            if (idMenus[i] == id) {
                ((TextView) findViewById(idMenus[i]))
                        .setTextColor(Color.BLACK);
                if (getSupportActionBar() != null) {
                    ctlMain.setTitle(ttMenus[i]);       //Toolbar包裹在CollapsingToolbarLayout里面时需要
                                                        //通过CollapsingToolbarLayout来设置title
                }
                fragmentManager.beginTransaction().replace(R.id.fl_content, fragmentList.get(i)).commit();
                fragmentId = i;
                invalidateOptionsMenu();    //完成fragmentId的设置后刷新菜单
                ivBackground.setImageResource(bgToolbar[i]);
                if (id == R.id.btn_setting) {
                    fabAdd.setVisibility(View.GONE);
                } else {
                    fabAdd.setVisibility(View.VISIBLE);
                }
            } else {    //如果取到的ID不是被点击的ID，直接把该菜单颜色设置为浅色以表示未选中即可
                ((TextView) findViewById(idMenus[i])).setTextColor(0xff9e9e9e);     //grey_500
            }
        }
        slidingMenu.showContent();
    }

    //删除提示弹窗点击事件
    @Override
    public void onClick(DialogInterface dialog, int which) {
        SQLiteDatabase database = new DatabaseHelper(this, "Moji.db", null, 1).getWritableDatabase();
        BaseFragment baseFragment = fragmentId == FRAGMENT_NOTE ? noteFragment : diaryFragment;
        //对list进行升序排序，使得删除自顶向下，以保证删除过程中position不会出问题
        Collections.sort(baseFragment.deleteList);
        Collections.sort(baseFragment.positionList);
        for (int i = 0; i < baseFragment.deleteList.size(); i ++) {
            if (fragmentId == FRAGMENT_NOTE) {
                database.delete("note", "id = ?", new String[]{baseFragment.deleteList.get(i)});
                noteFragment.flush(FLUSH_REMOVE, baseFragment.positionList.get(i) - i);
            } else {
                database.delete("diary", "id = ?", new String[]{baseFragment.deleteList.get(i)});
                diaryFragment.flush(FLUSH_REMOVE, baseFragment.positionList.get(i) - i);
            }
        }
        //情况delete列表，切换回普通状态
        baseFragment.deleteList.clear();
        baseFragment.positionList.clear();
        baseFragment.isDeleting = false;
        fabAdd.setImageResource(R.mipmap.ic_add_white_24dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (fragmentId != FRAGMENT_DIARY) {    //非日记无需设置日历
            menu.findItem(R.id.action_calendar).setVisible(false);
        }
        return true;
    }

    //ActionBar菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_calendar:
                startActivity(new Intent(this, CalendarActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //当由当前fragment切换至其他fragment时需要清空删除list
        BaseFragment baseFragment = fragmentId == FRAGMENT_NOTE ? noteFragment : diaryFragment;
        baseFragment.deleteList.clear();
        baseFragment.positionList.clear();
        //刷新内容部分
        switch (fragmentId) {
            case FRAGMENT_NOTE:
                noteFragment.flush(FLUSH_ALL, 0);
                break;
            case FRAGMENT_DIARY:
                diaryFragment.flush(FLUSH_ALL, 0);
                break;
        }
        //刷新抽屉部分
        if (preferences.getString("avatar", null) != null) {
            sdvAvatar.setImageURI(Uri.parse(preferences.getString("avatar", null)));
        }
        tvNickname.setText(preferences.getString("nickname", "昵称"));
        tvSignature.setText(preferences.getString("signature", "还没有个性签名"));
        MobclickAgent.onResume(this);        //友盟用户统计
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);        //友盟用户统计
    }


    @Override
    public void onBackPressed() {
        if (slidingMenu.isMenuShowing()) {
            slidingMenu.showContent();
        } else {
            super.onBackPressed();
        }
    }

    //反馈消息未读数目成功获取后需要发出一条Notification
    @Override
    public void onSuccess(Object... objects) {
        if ((int)objects[0] > 0) {
            //创建Notification的跳转Intent
            Intent intentNotify = new Intent(this, FeedbackReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    intentNotify,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            //创建Notification
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.small_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.feedback_notify, (int)objects[0]))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
            notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
            //发出Notification
            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, notification);
        }
    }
    @Override
    public void onError(int i, String s) {}
    @Override
    public void onProgress(int i) {}
}
