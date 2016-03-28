package net.roocky.moji.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import net.roocky.moji.Database.DatabaseHelper;
import net.roocky.moji.Fragment.DiaryFragment;
import net.roocky.moji.Fragment.NoteFragment;
import net.roocky.moji.R;
import net.roocky.moji.Fragment.SettingFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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

    private final int FRAGMENT_DIARY = 0;
    private final int FRAGMENT_NOTE = 1;
    private final int FRAGMENT_SETTING = 2;

    private SlidingMenu slidingMenu;        //侧滑菜单
    private CardView cvAccount;             //信息展示卡片
    private SimpleDraweeView sdvAvatar;     //用户头像
    private TextView tvNickname;            //用户昵称
    private TextView tvSignature;           //个性签名

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private DiaryFragment diaryFragment = new DiaryFragment();
    private NoteFragment noteFragment = new NoteFragment();
    private SettingFragment settingFragment = new SettingFragment();

    private int fragmentId = FRAGMENT_DIARY;         //记录当前所在的Fragment
    private List<Fragment> fragmentList = new ArrayList<>();        //存放日记、便笺、设置三个Fragment
    private String[] ttMenus = {"日記", "便箋", "設置"};
    private int[] idMenus = {R.id.btn_diary, R.id.btn_note, R.id.btn_setting};      //日记、便笺、设置三项的ID
    private int[] bgToolbar = {R.drawable.bd_diary, R.drawable.bd_note, R.drawable.bd_setting}; //Toolbar背景图片

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String uriAvatar;       //存在SharedPreferences中的头像Uri

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initExplain();          //使用说明初始化
        setSlidingMenu();       //设置SlidingMenu
        initView();             //View初始化
        setOnClickListener();

        itemSelected(R.id.btn_diary);   //设置默认“日记”项被选中
    }

    //初始化使用说明
    private void initExplain() {
        preferences = getSharedPreferences("moji", MODE_PRIVATE);
        editor = preferences.edit();

        if (preferences.getBoolean("isFirst", true)) {
            editor.putBoolean("isFirst", false).apply();   //设置一个布尔变量标识是否第一次运行App

            DatabaseHelper databaseHelper = new DatabaseHelper(this, "Moji.db", null, 1);
            SQLiteDatabase database = databaseHelper.getWritableDatabase();
            String[] numbers = getResources().getStringArray(R.array.number_array);
            //向数据库中存入使用说明
            ContentValues values = new ContentValues();
            values.put("time", numbers[Calendar.getInstance().get(Calendar.MONTH)] +
                    "\n · \n" + numbers[Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1]);
            values.put("content", getString(R.string.use_explain_diary_1));
            database.insert("diary", null, values);
            database.insert("note", null, values);
            values.clear();
            values.put("time", numbers[Calendar.getInstance().get(Calendar.MONTH)] +
                    "\n · \n" + numbers[Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1]);
            values.put("content", getString(R.string.use_explain_diary_2));
            database.insert("diary", null, values);
            database.insert("note", null, values);
        }
    }

    //初始化View
    private void initView() {
        cvAccount = (CardView)findViewById(R.id.cv_account);
        sdvAvatar = (SimpleDraweeView)findViewById(R.id.sdv_avatar);
        tvNickname = (TextView)findViewById(R.id.tv_nickname);
        tvSignature = (TextView)findViewById(R.id.tv_signature);

        setSupportActionBar(toolbar);
        uriAvatar = preferences.getString("avatar", null);
        if (uriAvatar != null) {
//            sdvAvatar.setImageURI(Uri.parse(uriAvatar));
        }

        fragmentManager.beginTransaction().replace(R.id.fl_content, diaryFragment).commit();
        fragmentList.add(diaryFragment);
        fragmentList.add(noteFragment);
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
        cvAccount.setOnClickListener(this);
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
            case R.id.cv_account:
                startActivity(new Intent(this, AccountActivity.class));
                break;
            case R.id.fab_add:
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                //新建日记和便笺的Activity为同一个，但是Intent携带参数不同
                switch (fragmentId) {
                    case FRAGMENT_DIARY:
                        intent.putExtra("from", "diary");
                        break;
                    case FRAGMENT_NOTE:
                        intent.putExtra("from", "note");
                        break;
                }
                startActivity(intent);
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
                        .setTextColor(getResources().getColor(android.R.color.black));  //在API 23中被弃用
                if (getSupportActionBar() != null) {
                    ctlMain.setTitle(ttMenus[i]);       //Toolbar包裹在CollapsingToolbarLayout里面时需要
                                                        //通过CollapsingToolbarLayout来设置title
                }
                fragmentManager.beginTransaction().replace(R.id.fl_content, fragmentList.get(i)).commit();
                fragmentId = i;
                ivBackground.setImageResource(bgToolbar[i]);
                if (id == R.id.btn_setting) {
                    fabAdd.setVisibility(View.GONE);
                } else {
                    fabAdd.setVisibility(View.VISIBLE);
                }
            } else {    //如果取到的ID不是被点击的ID，直接把该菜单颜色设置为浅色以表示未选中即可
                ((TextView) findViewById(idMenus[i])).setTextColor(getResources().getColor(R.color.grey_500));
            }
        }
        slidingMenu.showContent();
    }

    @Override
    protected void onResume() {
        //刷新内容部分
        switch (fragmentId) {
            case FRAGMENT_DIARY:
                diaryFragment.flush();
                break;
            case FRAGMENT_NOTE:
                noteFragment.flush();
                break;
        }
        //刷新抽屉部分
        if (uriAvatar != null) {
//            sdvAvatar.setImageURI(Uri.parse(preferences.getString("avatar", null)));
        }
        tvNickname.setText(preferences.getString("nickname", "昵称"));
        tvSignature.setText(preferences.getString("signature", "还没有个性签名"));

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (slidingMenu.isMenuShowing()) {
            slidingMenu.showContent();
        } else {
            super.onBackPressed();
        }
    }
}
