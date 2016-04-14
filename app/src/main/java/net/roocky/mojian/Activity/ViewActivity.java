package net.roocky.mojian.Activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.umeng.analytics.MobclickAgent;

import net.roocky.mojian.BroadcastReceiver.RemindReceiver;
import net.roocky.mojian.Database.DatabaseHelper;
import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;
import net.roocky.mojian.Util.SoftInput;

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
        TimePickerDialog.OnTimeSetListener {
    @Bind(R.id.abl_toolbar)
    AppBarLayout ablToolbar;
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
    @Bind(R.id.iv_weather)
    ImageView ivWeather;
    @Bind(R.id.tv_year)
    TextView tvYear;
    @Bind(R.id.tv_month_day)
    TextView tvMonthDay;
    @Bind(R.id.iv_weather_icon)
    ImageView ivWeatherIcon;

    private Intent intent;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private boolean isEdit = false;     //标识当前是否为编辑状态

    private AlertDialog dialogDiary;
    private AlertDialog dialogNote;
    private AlertDialog dialogUpdate;

    private int yearRemind;
    private int monthRemind;
    private int dayRemind;

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
        databaseHelper = new DatabaseHelper(this, "Mojian.db", null, 1);
        database = databaseHelper.getWritableDatabase();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.app_name));
        }
        //显示内容
        tvContent.setText(intent.getStringExtra("content"));
        if (intent.getStringExtra("from").equals("diary")) {
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
            tvRemind.setText(getString(R.string.note_remind, intent.getStringExtra("remind")));
        }
    }

    private void setListener() {
        fabEdit.setOnClickListener(this);
        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.nsv_content);
        scrollView.setOnScrollChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        if (intent.getStringExtra("from").equals("diary")) {    //日记无需设置提醒
            menu.findItem(R.id.action_remind).setVisible(false);
        }
        return true;
    }

    //ActionBar菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:     //返回箭头
                if (intent.getStringExtra("from").equals("note") && isEdit) {//便笺的编辑状态并未修改Navigation图标，所以需要在此处保存
                    ContentValues values = new ContentValues();
                    values.put("content", etContent.getText().toString());
                    database.update("note", values, "id = ?", new String[]{intent.getStringExtra("id")});
                }
                finish();
                break;
            case R.id.action_delete:    //删除
                if (intent.getStringExtra("from").equals("diary")) {
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
            case R.id.action_remind:
                new DatePickerDialog(
                        this,
                        this,
                        Mojian.year,
                        Mojian.month,
                        Mojian.day).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //其他View点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_edit:
                if (intent.getStringExtra("from").equals("diary")) {    //日记编辑状态需要改变Navigation图标
                    toolbar.setNavigationIcon(R.mipmap.ic_done_black_24dp);
                    toolbar.setNavigationOnClickListener(this);
                }

                ablToolbar.setExpanded(false);
                tvContent.setVisibility(View.GONE);
                etContent.setText(tvContent.getText());
                etContent.setVisibility(View.VISIBLE);
                etContent.requestFocus();
                fabEdit.hide();
                SoftInput.show(etContent);  //显示软键盘

                isEdit = true;
                break;
            default:       //保存点击事件
                if (isEdit) {
                    ContentValues values = new ContentValues();
                    values.put("content", etContent.getText().toString());
                    if (intent.getStringExtra("from").equals("diary")) {
                        database.update("diary", values, "id = ?", new String[]{intent.getStringExtra("id")});
                    } else {
                        database.update("note", values, "id = ?", new String[]{intent.getStringExtra("id")});
                    }

                    toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_black_24dp);
                    toolbar.setNavigationOnClickListener(this);

                    etContent.setVisibility(View.GONE);
                    tvContent.setText(etContent.getText().toString());
                    tvContent.setVisibility(View.VISIBLE);
                    SoftInput.hide(etContent);
                    fabEdit.show();

                    isEdit = false;
                } else {            //未处于编辑状态需要销毁当前Activity
                    finish();
                }
                break;
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
                database.update("diary", values, "id = ?", new String[]{intent.getStringExtra("id")});
            }
        }
        finish();
    }

    //便笺提醒选择器设置监听
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        if (isEdit) {
            if (intent.getStringExtra("from").equals("note")) {
                ContentValues values = new ContentValues();
                values.put("content", etContent.getText().toString());
                database.update("note", values, "id = ?", new String[]{intent.getStringExtra("id")});
                super.onBackPressed();
            } else {
                if (!tvContent.getText().toString().equals(etContent.getText().toString())) {
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
}
