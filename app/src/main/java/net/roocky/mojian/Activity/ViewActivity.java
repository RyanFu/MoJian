package net.roocky.mojian.Activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import net.roocky.mojian.BroadcastReceiver.RemindReceiver;
import net.roocky.mojian.Database.DatabaseHelper;
import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;
import net.roocky.mojian.Util.BitmapUtil;
import net.roocky.mojian.Util.PermissionUtil;
import net.roocky.mojian.Util.ScreenUtil;
import net.roocky.mojian.Util.SoftInput;
import net.roocky.mojian.Widget.SelectDialog;

import java.io.FileNotFoundException;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by roocky on 03/28.
 * 查看内容
 */
public class ViewActivity extends AppCompatActivity implements View.OnClickListener,
        DialogInterface.OnClickListener,
        NestedScrollView.OnScrollChangeListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        ViewTreeObserver.OnGlobalLayoutListener,
        SelectDialog.OnItemClickListener{
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.fab_edit)
    FloatingActionButton fabEdit;
    @Bind(R.id.tv_remind)
    TextView tvRemind;
    @Bind(R.id.rl_header)
    RelativeLayout rlHeader;
    @Bind(R.id.fl_content)
    FrameLayout flContent;
    @Bind(R.id.iv_weather)
    ImageView ivWeather;
    @Bind(R.id.tv_year)
    TextView tvYear;
    @Bind(R.id.tv_month_day)
    TextView tvMonthDay;
    @Bind(R.id.iv_weather_icon)
    ImageView ivWeatherIcon;
    @Bind(R.id.nsv_content)
    NestedScrollView nsvContent;
    @Bind(R.id.iv_bottom)
    ImageView ivBottom;
    @Bind(R.id.iv_background)
    ImageView ivBackground;
    @Bind(R.id.cl_main)
    CoordinatorLayout clMain;

    private Intent intent;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private boolean isEdit = false;     //标识当前是否为编辑状态

    private AlertDialog dialogDiary;
    private AlertDialog dialogNote;
    private AlertDialog dialogUpdate;
    private DatePickerDialog remindPicker;
    private DatePickerDialog datePicker;

    private int yearRemind;
    private int monthRemind;
    private int dayRemind;

    private final int PER_EXTERNAL_STORAGE = 0;
    private Bitmap bmpContent;

    private int[] weathers = {
            R.drawable.wd_weather_sun,
            R.drawable.wd_weather_clouds,
            R.drawable.wd_weather_rain,
            R.drawable.wd_weather_snow
    };
    private int[] weatherIcons = {
            R.drawable.weather_sun,
            R.drawable.weather_clouds,
            R.drawable.weather_clouds_with_rain,
            R.drawable.weather_clouds_with_snow
    };
    private int background = 0;     //标识当前背景
    private String from;

    private final int SELECT_IMAGE = 0;
    private final int INIT_CONTENT = 1;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_CONTENT:
                    tvContent.setText((SpannableStringBuilder)msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);

        initView();
        setListener();
    }

    private void initView() {
        intent = getIntent();
        from = intent.getStringExtra("from");
        background = intent.getIntExtra("background", 0);
        databaseHelper = new DatabaseHelper(this, "Mojian.db", null, 2);
        database = databaseHelper.getWritableDatabase();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.app_name));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = INIT_CONTENT;
                message.obj = initContent();
                handler.sendMessage(message);
            }
        }).run();
        ivBackground.setImageResource(Mojian.backgrounds[background]);
        ivBottom.setImageResource(Mojian.backgrounds[background]);
        if (from.equals("diary")) {
            rlHeader.setVisibility(View.VISIBLE);
            //设置顶部日期&天气展示
            ivWeather.setImageResource(weathers[intent.getIntExtra("weather", 0)]);
            tvYear.setText(getResources().getStringArray(R.array.year_array)[intent.getIntExtra("year", 2016) - 2010]);
            tvMonthDay.setText(getString(R.string.diary_month_day,
                    getResources().getStringArray(R.array.number_array)[intent.getIntExtra("month", 0)],
                    getResources().getStringArray(R.array.number_array)[intent.getIntExtra("day", 1) - 1]));
            ivWeatherIcon.setImageResource(weatherIcons[intent.getIntExtra("weather", 0)]);
        } else if (!intent.getStringExtra("remind").equals("")) {
            //设置提醒语句
            tvRemind.setVisibility(View.VISIBLE);
            tvRemind.setText(getString(R.string.note_remind, intent.getStringExtra("remind")));
        }
    }

    //初始化内容
    private SpannableStringBuilder initContent() {
        SpannableStringBuilder ssbContent = new SpannableStringBuilder();
        String strContent = intent.getStringExtra("content");
        StringBuilder strUrl = new StringBuilder();
        boolean isUrl = false;
        for (int i = 0; i < strContent.length(); i ++) {
            if (strContent.charAt(i) == '<') {          //路径开始标志
                isUrl = true;
            } else if (strContent.charAt(i) == '>') {   //路径结束标志
                String tag = "<" + strUrl + ">";
                Bitmap bitmap = BitmapFactory.decodeFile(strUrl.toString());
                if (bitmap == null) {                   //该路径并不是一个真实的图片路径
                    ssbContent.append("<").append(strUrl).append(">");
                    strUrl.delete(0, strUrl.length());
                    isUrl = false;
                    continue;
                }
                ImageSpan imageSpan = new ImageSpan(this, bitmap);
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

    private void setListener() {
        fabEdit.setOnClickListener(this);
        nsvContent = (NestedScrollView) findViewById(R.id.nsv_content);
        nsvContent.setOnScrollChangeListener(this);
        clMain.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        if (from.equals("diary")) {    //日记无需设置提醒
            menu.findItem(R.id.action_remind).setVisible(false);
        }
        if (isEdit) {
            if (from.equals("diary")) {
                menu.findItem(R.id.action_date).setVisible(true);       //修改日期
            }
            menu.findItem(R.id.action_background).setVisible(true);       //修改日期
        } else {
            if (from.equals("diary")) {
                menu.findItem(R.id.action_date).setVisible(false);
            }
            menu.findItem(R.id.action_background).setVisible(false);
        }

        return true;
    }

    //ActionBar菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:     //返回箭头
                if (from.equals("note") && isEdit) {//便笺的编辑状态并未修改Navigation图标，所以需要在此处保存
                    ContentValues values = new ContentValues();
                    values.put("content", etContent.getText().toString());
                    values.put("background", background);
                    database.update("note", values, "id = ?", new String[]{intent.getStringExtra("id")});
                }
                finish();
                break;
            case R.id.action_background:
                SelectDialog selectDialog = new SelectDialog(this, R.style.Widget_SelectDialog, R.layout.dialog_background);
                Window window = selectDialog.getWindow();
                window.setGravity(Gravity.TOP | Gravity.RIGHT);
                selectDialog.show();
                selectDialog.setOnItemClickListener(selectDialog, this);
                break;
            case R.id.action_date:
                datePicker = new DatePickerDialog(
                        this,
                        this,
                        Mojian.year,
                        Mojian.month,
                        Mojian.day
                );
                datePicker.show();
                break;
            case R.id.action_delete:    //删除
                if (from.equals("diary")) {
                    dialogDiary = new AlertDialog.Builder(this)
                            .setTitle("删除")
                            .setMessage("确定删除该日记吗？")
                            .setPositiveButton("确定", this)
                            .setNegativeButton("取消", null)
                            .show();
                } else {
                    dialogNote = new AlertDialog.Builder(this)
                            .setTitle("删除")
                            .setMessage("确定删除该便笺吗？")
                            .setPositiveButton("确定", this)
                            .setNegativeButton("取消", null)
                            .show();
                }
                break;
            case R.id.action_share_picture:
                if (ivBottom.getVisibility() == View.GONE) {
                    ivBottom.setVisibility(View.VISIBLE);
                    ivBackground.setVisibility(View.GONE);
                }
                int width = ScreenUtil.getWidth(this);                  //获取屏幕宽度
                bmpContent = ScreenUtil.screenshot(findViewById(R.id.nsv_content), width); //截长图
                //先检查权限在进行保存
                if (PermissionUtil.checkA(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, PER_EXTERNAL_STORAGE)) {
                    long currentTimeMill = BitmapUtil.save(bmpContent, getString(R.string.path_cache), 80);     //保存至SD卡
                    if (currentTimeMill != 0) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM,
                                Uri.parse("file:///" + Environment.getExternalStorageDirectory()
                                        + getString(R.string.path_cache)
                                        + currentTimeMill + ".jpg"));
                        shareIntent.setType("image/*");
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share_picture)));
                    } else {
                        Snackbar.make(toolbar, getString(R.string.toast_to_picture_error), Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.action_share_text:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        tvContent.getText());
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share_text)));
                break;
            case R.id.action_remind:
                remindPicker = new DatePickerDialog(
                        this,
                        this,
                        Mojian.year,
                        Mojian.month,
                        Mojian.day);
                remindPicker.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //处理Android 6.0中permission请求完成事件
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PER_EXTERNAL_STORAGE:  //存储空间权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    long currentTimeMill = BitmapUtil.save(bmpContent, getString(R.string.path_cache), 80);     //保存至SD卡
                    if (currentTimeMill != 0) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM,
                                Uri.parse("file:///" + Environment.getExternalStorageDirectory()
                                        + getString(R.string.path_cache)
                                        + currentTimeMill + ".jpg"));
                        shareIntent.setType("image/*");
                        startActivity(Intent.createChooser(shareIntent, "图片分享"));
                    } else {
                        Snackbar.make(toolbar, getString(R.string.toast_to_picture_error), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(toolbar, getString(R.string.toast_per_fail), Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //其他View点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_edit:
                if (isEdit) {       //添加图片
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_IMAGE);
                } else {            //切换至编辑状态
                    isEdit = true;
                    if (from.equals("diary")) {    //日记编辑状态需要改变Navigation图标
                        toolbar.setNavigationIcon(R.mipmap.ic_done_black_24dp);
                        toolbar.setNavigationOnClickListener(this);
                    }
                    //更新menu
                    invalidateOptionsMenu();
                    //将背景图片“移到”TextView底部
                    ivBackground.setVisibility(View.GONE);
                    ivBottom.setVisibility(View.VISIBLE);
                    //将TextView“转为”EditText
                    tvContent.setVisibility(View.GONE);
                    etContent.setText(tvContent.getText());
                    etContent.setVisibility(View.VISIBLE);
                    etContent.requestFocus();
                    fabEdit.setImageResource(R.mipmap.ic_add_white_24dp);
                    SoftInput.show(etContent);  //显示软键盘
                }
                break;
            default:       //toolbar保存点击事件
                if (isEdit) {
                    ContentValues values = new ContentValues();
                    values.put("content", etContent.getText().toString());
                    values.put("background", background);
                    if (from.equals("diary")) {
                        database.update("diary", values, "id = ?", new String[]{intent.getStringExtra("id")});
                    } else {
                        database.update("note", values, "id = ?", new String[]{intent.getStringExtra("id")});
                    }

                    toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_black_24dp);
                    toolbar.setNavigationOnClickListener(this);
                    //将EditText“转为”TextView
                    etContent.setVisibility(View.GONE);
                    tvContent.setText(etContent.getText().toString());
                    tvContent.setVisibility(View.VISIBLE);
                    SoftInput.hide(etContent);
                    fabEdit.setImageResource(R.mipmap.ic_edit_white_24dp);

                    isEdit = false;
                    invalidateOptionsMenu();
                } else {            //未处于编辑状态需要销毁当前Activity
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            try {   //Bitmap压缩
                bitmap = BitmapUtil.compress(this, getContentResolver().openInputStream(data.getData()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            long currentTimeMill = BitmapUtil.save(bitmap, getString(R.string.path_pic), 40);   //保存至本地
            ImageSpan imageSpan = new ImageSpan(this, bitmap);
            String tag = "<"
                    + Environment.getExternalStorageDirectory() + getString(R.string.path_pic) + currentTimeMill
                    + ".jpg"
                    + ">";
            SpannableString spannableString = new SpannableString(tag);
            spannableString.setSpan(imageSpan, 0, tag.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            int index = etContent.getSelectionStart();
            Editable editable = etContent.getEditableText();
            if (index < 0 || index >= editable.length()) {
                editable.append(spannableString);
            } else {
                editable.insert(index, spannableString);
            }
        }
    }

    //Dialog点击事件
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (dialog.equals(dialogDiary)) {   //删除日记
            database.delete("diary", "id = ?", new String[]{intent.getStringExtra("id")});
        } else if (dialog.equals(dialogNote)){                            //删除便笺
            database.delete("note", "id = ?", new String[]{intent.getStringExtra("id")});
        } else {                //是否保存修改
            if (which == DialogInterface.BUTTON_POSITIVE) {
                ContentValues values = new ContentValues();
                values.put("content", etContent.getText().toString());
                values.put("background", background);
                database.update("diary", values, "id = ?", new String[]{intent.getStringExtra("id")});
            }
        }
        finish();
    }

    //背景选择dialog点击事件
    @Override
    public void onItemClick(SelectDialog dialog, int position) {
        background = position;
        ivBackground.setImageResource(Mojian.backgrounds[background]);
        ivBottom.setImageResource(Mojian.backgrounds[background]);
        dialog.dismiss();
    }

    //便笺提醒选择器设置监听
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (from.equals("note")) {      //便笺设置提醒
            yearRemind = year;
            monthRemind = monthOfYear;
            dayRemind = dayOfMonth;
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    this,
                    Mojian.hour,
                    Mojian.minute,
                    false);
            timePickerDialog.setCancelable(false);
            timePickerDialog.show();
        } else {                        //日记修改日期
            ContentValues values = new ContentValues();
            values.put("year", year);
            values.put("month", monthOfYear);
            values.put("day", dayOfMonth);
            database.update("diary", values, "id = ?", new String[]{intent.getStringExtra("id")});
            tvYear.setText(getResources().getStringArray(R.array.year_array)[year - 2010]);
            tvMonthDay.setText(getString(R.string.diary_month_day,
                    getResources().getStringArray(R.array.number_array)[monthOfYear],
                    getResources().getStringArray(R.array.number_array)[dayOfMonth - 1]));
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
        String requestCode = intent.getStringExtra("id");

        int month = monthRemind + 1;
        String hour = String.valueOf(hourOfDay);
        String minute = String.valueOf(minuteOfHour);
        //如果小时或分钟为个位数，需补“0”
        if (hourOfDay < 10) {
            hour = "0" + hourOfDay;
        }
        if (minuteOfHour < 10) {
            minute = "0" + minuteOfHour;
        }
        String strRemind =                  //存入数据库的提醒时间
                yearRemind + "年"
                        + month + "月"
                        + dayRemind + "日"
                        + " "
                        + hour
                        + " : "
                        + minute;
        tvRemind.setVisibility(View.VISIBLE);
        tvRemind.setText(getString(R.string.note_remind, strRemind));
        ContentValues values = new ContentValues();
        values.put("remind", strRemind);
        database.update("note", values, "id = ?", new String[]{intent.getStringExtra("id")});   //更新数据库中便笺提醒时间
        //设置提醒时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR, yearRemind);
        calendar.set(Calendar.MONTH, monthRemind);
        calendar.set(Calendar.DAY_OF_MONTH, dayRemind);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minuteOfHour);

        Intent intentReceiver = new Intent(this, RemindReceiver.class);
        intentReceiver.putExtra("from", "note");
        intentReceiver.putExtra("id", requestCode);
        intentReceiver.putExtra("content", intent.getStringExtra("content"));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,       //Context
                Integer.parseInt(requestCode),      //requestCode
                intentReceiver,     //Intent
                PendingIntent.FLAG_UPDATE_CURRENT);     //Flag

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);   //开启定时
        }
    }

    //NestedScrollView滚动事件
    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (!isEdit) {
            if (scrollY - oldScrollY > 0) {
                fabEdit.hide();
            } else {
                fabEdit.show();
            }
        }
    }

    //界面view变化监听
    @Override
    public void onGlobalLayout() {
        //根据软键盘是否显示决定背景图片的位置
        if (ScreenUtil.isSoftInputShow(clMain)) {
            ivBackground.setVisibility(View.GONE);
            ivBottom.setVisibility(View.VISIBLE);
        } else {
            if (toolbar.getMeasuredHeight() + rlHeader.getMeasuredHeight() + flContent.getMeasuredHeight()
                    + ivBackground.getMeasuredHeight() > ScreenUtil.getHeight(this) - 200) {//如果TextView过长需要隐藏background显示Bottom
                ivBackground.setVisibility(View.GONE);
                ivBottom.setVisibility(View.VISIBLE);
            } else {
                ivBackground.setVisibility(View.VISIBLE);
                ivBottom.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        if (isEdit) {
            if (from.equals("note")) {
                ContentValues values = new ContentValues();
                values.put("content", etContent.getText().toString());
                values.put("background", background);
                database.update("note", values, "id = ?", new String[]{intent.getStringExtra("id")});
                super.onBackPressed();
            } else {
                if (!tvContent.getText().toString().equals(etContent.getText().toString()) ||
                        background != intent.getIntExtra("background", 0)) {
                    dialogUpdate = new AlertDialog.Builder(this)
                            .setTitle("修改")
                            .setMessage("需要保存修改吗？")
                            .setPositiveButton("保存", this)
                            .setNegativeButton("不保存", this)
                            .setCancelable(false)
                            .show();
                } else {
                    super.onBackPressed();
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            clMain.getViewTreeObserver().removeOnGlobalLayoutListener(this);        //防止内存泄漏
        } else {
            clMain.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }
}
