package net.roocky.mojian.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import net.roocky.mojian.Database.DatabaseHelper;
import net.roocky.mojian.Fragment.BaseFragment;
import net.roocky.mojian.Fragment.DiaryFragment;
import net.roocky.mojian.Fragment.NoteFragment;
import net.roocky.mojian.Fragment.SettingFragment;
import net.roocky.mojian.Model.Diary;
import net.roocky.mojian.Model.Note;
import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;
import net.roocky.mojian.Util.FileUtil;
import net.roocky.mojian.Util.SoftInput;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements
        View.OnClickListener,
//        IWxCallback,
        DialogInterface.OnClickListener {

    @Bind(R.id.iv_background)
    ImageView ivBackground;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.ctl_main)
    CollapsingToolbarLayout ctlMain;
    @Bind(R.id.fab_add)
    FloatingActionButton fabAdd;

    private final int FRAGMENT_NOTE = 0;
    private final int FRAGMENT_DIARY = 1;
    private final int FRAGMENT_SETTING = 2;

    private SlidingMenu slidingMenu;        //侧滑菜单
    private SimpleDraweeView sdvAvatar;     //用户头像
    private TextView tvNickname;            //用户昵称
    private EditText etNickname;
    private TextView tvSignature;           //个性签名
    private EditText etSignature;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private static NoteFragment noteFragment = new NoteFragment();
    private static DiaryFragment diaryFragment = new DiaryFragment();
    private SettingFragment settingFragment = new SettingFragment();
    private static BaseFragment baseFragment;

    private List<Fragment> fragmentList = new ArrayList<>();        //存放便笺、日记、设置三个Fragment
    private String[] ttMenus = {"便笺", "日记", "设置"};
    private int[] idMenus = {R.id.btn_note, R.id.btn_diary, R.id.btn_setting};      //便笺、日记、设置三项的ID
    private int[] bgToolbar = {R.drawable.bd_note, R.drawable.bd_diary, R.drawable.bd_setting}; //Toolbar背景图片

    private AlertDialog dialogDelete;
    private AlertDialog dialogNickname;
    private AlertDialog dialogSignature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        FeedbackAPI.getFeedbackUnreadCount(this, null, this);   //检查百川反馈未读消息
        initExplain();          //使用说明初始化
        setSlidingMenu();       //设置SlidingMenu
        initView();             //View初始化
        setOnClickListener();

        itemSelected(R.id.btn_note);   //设置默认“便笺”项被选中
    }

    //初始化使用说明
    private void initExplain() {
        if (preferences.getBoolean("isFirst", true)) {
            editor.putBoolean("isFirst", false).apply();   //设置一个布尔变量标识是否第一次运行App

            try {
                File dir = new File(getString(R.string.path_databases));
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                InputStream inputStream = getAssets().open("Mojian.db");
                FileUtil.copy(inputStream, getString(R.string.path_databases) + "Mojian.db");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //初始化View
    private void initView() {
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
        sdvAvatar.setOnClickListener(this);
        tvNickname.setOnClickListener(this);
        tvSignature.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        ivBackground.setOnClickListener(this);      //更改背景图片
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
            case R.id.sdv_avatar:
                setWhat = AVATAR;
                dialogAvatar = new AlertDialog.Builder(this)
                        .setTitle("更改头像")
                        .setItems(new String[]{"拍照", "从相册中选中"}, this)
                        .show();
                break;
            case R.id.tv_nickname:
                dialogNickname = new AlertDialog.Builder(this)
                        .setTitle("昵称")
                        .setView(etNickname = new EditText(this))
                        .setPositiveButton("确定", this)
                        .setNegativeButton("取消", null)
                        .show();
                etNickname.setText(preferences.getString("nickname", ""));
                SoftInput.show(etNickname);     //自动打开软键盘
                etNickname.requestFocus();
                break;
            case R.id.tv_signature:
                dialogSignature = new AlertDialog.Builder(this)
                        .setTitle("个性签名")
                        .setView(etSignature = new EditText(this))
                        .setPositiveButton("确定", this)
                        .setNegativeButton("取消", null)
                        .show();
                etSignature.setText(preferences.getString("signature", ""));
                SoftInput.show(etSignature);     //自动打开软键盘
                etSignature.requestFocus();
                break;
            case R.id.fab_add:
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
                break;
            case R.id.iv_background:        //修改背景图片
                setWhat = BACKGROUND;
                dialogBackground = new AlertDialog.Builder(this)
                        .setTitle("更改背景")
                        .setItems(new String[]{"拍照", "从相册中选中", "恢复默认背景"}, this)
                        .show();
                break;
            default:
                itemSelected(v.getId());
                break;
        }
    }

    //当菜单子项被选中时进行的操作
    private void itemSelected(int id) {
        if (id == R.id.btn_diary && !preferences.getString("patternSha1", "").equals("") && Mojian.isLocked) {
            startActivityForResult(new Intent(this, PatternConfirmActivity.class), PATTERN_LOCK_DIARY);
        } else {
            for (int i = 0; i < idMenus.length; i++) {
                /**
                 * 如果取到的ID等于被点击的ID，则需将被点击的菜单颜色设置成黑色来凸显当前所选择的菜单，把Toolbar的title
                 * 设置为相应的文字。再进行Fragment替换操作，然后把fragmentId设置为该ID以表示当前所在的Fragment，根据
                 * 所在Fragment设置对应的Toolbar背景图片。如果所在Fragment为“设置”则不需要显示FloatActionBar
                 */
                if (idMenus[i] != id) {     //如果取到的ID不是被点击的ID，直接把该菜单颜色设置为浅色以表示未选中即可
                    ((TextView) findViewById(idMenus[i])).setTextColor(0xff9e9e9e);     //grey_500
                } else {
                    ((TextView) findViewById(idMenus[i])).setTextColor(Color.BLACK);
                    if (getSupportActionBar() != null) {
                        ctlMain.setTitle(ttMenus[i]);
                    }
                    fragmentManager.beginTransaction().replace(R.id.fl_content, fragmentList.get(i)).commit();
                    fragmentId = i;
                    if (!preferences.getString("background" + i, "").equals("")) {        //用户有自定义背景图片
                        ivBackground.setImageURI(Uri.parse(preferences.getString("background" + i, "")));
                    } else {        //用户未自定义背景图片
                        ivBackground.setImageResource(bgToolbar[i]);
                    }
                    if (id == R.id.btn_setting) {
                        fabAdd.setVisibility(View.GONE);
                    } else {
                        fabAdd.setVisibility(View.VISIBLE);
                    }
                }
            }
            slidingMenu.showContent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PATTERN_LOCK_DIARY:
                if (resultCode == RESULT_OK) {
                    Mojian.isLocked = false;
                    ((TextView) findViewById(R.id.btn_diary)).setTextColor(Color.BLACK);
                    ((TextView) findViewById(R.id.btn_note)).setTextColor(0xff9e9e9e);     //grey_500
                    ((TextView) findViewById(R.id.btn_setting)).setTextColor(0xff9e9e9e);     //grey_500
                    slidingMenu.showContent();
                    if (getSupportActionBar() != null) {
                            ctlMain.setTitle(getString(R.string.mn_diary));
                    }
                    fragmentManager.beginTransaction().replace(R.id.fl_content, diaryFragment).commitAllowingStateLoss();
                    fragmentId = 1;
                    invalidateOptionsMenu();    //完成fragmentId的设置后刷新菜单
                    if (!preferences.getString("background" + 1, "").equals("")) {        //用户有自定义背景图片
                        ivBackground.setImageURI(Uri.parse(preferences.getString("background" + 1, "")));
                    } else {        //用户未自定义背景图片
                        ivBackground.setImageResource(R.drawable.bd_diary);
                    }
                    fabAdd.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    //设置头像
    @Override
    protected void setAvatar(Uri imageUri) {
        sdvAvatar.setImageURI(imageUri);
    }

    //设置背景图片
    @Override
    protected void setBackground(Uri imageUri) {
        if (imageUri.equals(Uri.parse(""))) {
            ivBackground.setImageResource(bgToolbar[fragmentId]);
        } else {
            ivBackground.setImageURI(imageUri);
        }
    }

    //弹窗点击事件
    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);           //更改头像&背景图片
        if (dialog.equals(dialogDelete)) {      //删除item
            final SQLiteDatabase database = new DatabaseHelper(this, "Mojian.db", null, 2).getWritableDatabase();
            //对List进行升序排序，使得删除自顶向下，以保证删除过程中position不会出问题
            Collections.sort(baseFragment.deleteList);
            Collections.sort(baseFragment.positionList);
            //对存放了被删除数据的List进行id升序排序，以便撤销时可以根据positionList来刷新RecyclerView
            if (fragmentId == FRAGMENT_NOTE) {
                Collections.sort(baseFragment.noteList, new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        return lhs.getId() - rhs.getId();
                    }
                });
                //清空Adapter的被选中的item的positionList（不是Fragment的positionList）
                noteFragment.getAdapter().setPositionList(new ArrayList<Integer>());
            } else {
                Collections.sort(baseFragment.diaryList, new Comparator<Diary>() {
                    @Override
                    public int compare(Diary lhs, Diary rhs) {
                        return lhs.getId() - rhs.getId();
                    }
                });
                //清空Adapter的被选中的item的positionList（不是Fragment的positionList）
                diaryFragment.getAdapter().setPositionList(new ArrayList<Integer>());
                //提前先获取到完整的tempList为后面删除刷新做准备
                diaryFragment.getAdapter().tempList = (List<Diary>) DatabaseHelper.query(database, "diary", null, null, null);
            }
            //从数据库中删除数据并刷新RecyclerView
            for (int i = 0; i < baseFragment.deleteList.size(); i++) {
                if (fragmentId == FRAGMENT_NOTE) {
                    database.delete("note", "id = ?", new String[]{baseFragment.deleteList.get(i)});
                    noteFragment.flush(Mojian.FLUSH_REMOVE, baseFragment.positionList.get(i) - i);
                } else {
                    database.delete("diary", "id = ?", new String[]{baseFragment.deleteList.get(i)});
                    //刷新至删除前所刷新到的地方
                    diaryFragment.flush(Mojian.FLUSH_REMOVE, baseFragment.positionList.get(i) - i, diaryFragment.count);
                }
            }
            //情况delete列表，切换回普通状态
            baseFragment.isDeleting = false;
            invalidateOptionsMenu();
            Snackbar.make(toolbar, getString(R.string.toast_delete_success), Snackbar.LENGTH_LONG)
                    .setAction("撤销", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ContentValues values = new ContentValues();
                            //恢复已删除的数据即再次将数据添加到数据库中
                            if (fragmentId == FRAGMENT_NOTE) {
                                for (int i = 0; i < baseFragment.noteList.size(); i++) {
                                    values.put("id", baseFragment.noteList.get(i).getId());
                                    values.put("year", baseFragment.noteList.get(i).getYear());
                                    values.put("month", baseFragment.noteList.get(i).getMonth());
                                    values.put("day", baseFragment.noteList.get(i).getDay());
                                    values.put("content", baseFragment.noteList.get(i).getContent());
                                    database.insert("note", null, values);
                                    noteFragment.flush(Mojian.FLUSH_ADD, baseFragment.positionList.get(i));
                                }
                            } else {
                                diaryFragment.getAdapter().tempList = (List<Diary>) DatabaseHelper.query(database, "diary", null, null, null);
                                for (int i = 0; i < baseFragment.diaryList.size(); i++) {
                                    values.put("id", baseFragment.diaryList.get(i).getId());
                                    values.put("year", baseFragment.diaryList.get(i).getYear());
                                    values.put("month", baseFragment.diaryList.get(i).getMonth());
                                    values.put("day", baseFragment.diaryList.get(i).getDay());
                                    values.put("weather", baseFragment.diaryList.get(i).getWeather());
                                    values.put("content", baseFragment.diaryList.get(i).getContent());
                                    database.insert("diary", null, values);
                                    diaryFragment.flush(Mojian.FLUSH_ADD, baseFragment.positionList.get(i), diaryFragment.count);
                                }
                            }
                        }
                    }).show();
        } else if (dialog.equals(dialogNickname)) {
            tvNickname.setText(etNickname.getText());
            editor.putString("nickname", etNickname.getText().toString()).commit();
        } else if (dialog.equals(dialogSignature)) {
            tvSignature.setText(etSignature.getText());
            editor.putString("signature", etSignature.getText().toString()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (fragmentId != FRAGMENT_DIARY) {    //非日记无需设置日历
            menu.findItem(R.id.action_calendar).setVisible(false);
        }
        if (baseFragment != null && baseFragment.isDeleting) {
            menu.findItem(R.id.action_delete).setVisible(true);
        } else {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return true;
    }

    //ActionBar菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                dialogDelete = new AlertDialog.Builder(this)
                        .setTitle("删除")
                        .setMessage("确定删除吗？")
                        .setPositiveButton("确定", this)
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.action_calendar:
                startActivity(new Intent(this, CalendarActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static BaseFragment getBaseFragment() {
        return baseFragment;
    }

    public static void setBaseFragment(BaseFragment fragment) {
        baseFragment = fragment;
    }

    public static NoteFragment getNoteFragment() {
        return noteFragment;
    }

    public static DiaryFragment getDiaryFragment() {
        return diaryFragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新抽屉部分
        if (preferences.getString("avatar", null) != null) {
            sdvAvatar.setImageURI(Uri.parse(preferences.getString("avatar", null)));
        }
        tvNickname.setText(preferences.getString("nickname", "昵称"));
        tvSignature.setText(preferences.getString("signature", "还没有个性签名"));
//        MobclickAgent.onResume(this);        //友盟用户统计
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);        //友盟用户统计
    }

    @Override
    public void onBackPressed() {
        if (slidingMenu.isMenuShowing()) {
            slidingMenu.showContent();
        } else {
            if (fragmentId != FRAGMENT_NOTE) {  //若点击返回键时不在便笺Fragment，需要先返回至便笺Fragment
                itemSelected(R.id.btn_note);
            } else {
                Mojian.isLocked = true;     //应用退出后需要将日记设定为锁定状态
                super.onBackPressed();
            }
        }
    }

    //反馈消息未读数目成功获取后需要发出一条Notification
//    @Override
//    public void onSuccess(Object... objects) {
//        if ((int)objects[0] > 0) {
//            //创建Notification的跳转Intent
//            Intent intentNotify = new Intent(this, FeedbackReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                    this,
//                    0,
//                    intentNotify,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//            //创建Notification
//            Notification notification = new NotificationCompat.Builder(this)
//                    .setContentIntent(pendingIntent)
//                    .setSmallIcon(R.drawable.small_icon)
//                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
//                    .setContentTitle(getString(R.string.app_name))
//                    .setContentText(getString(R.string.feedback_notify, (int)objects[0]))
//                    .setAutoCancel(true)
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .build();
//            notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
//            //发出Notification
//            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//            manager.notify(0, notification);
//        }
//    }
//    @Override
//    public void onError(int i, String s) {}
//    @Override
//    public void onProgress(int i) {}
}
