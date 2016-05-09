package net.roocky.mojian.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.roocky.mojian.Database.DatabaseHelper;
import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;
import net.roocky.mojian.Util.BitmapUtil;
import net.roocky.mojian.Util.PermissionUtil;
import net.roocky.mojian.Util.SDKVersion;
import net.roocky.mojian.Util.ScreenUtil;
import net.roocky.mojian.Util.SoftInput;
import net.roocky.mojian.Widget.AlignImageSpan;
import net.roocky.mojian.Widget.SelectDialog;

import java.io.FileNotFoundException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by roocky on 03/28.
 * 新建
 */
public class AddActivity extends AppCompatActivity implements
        View.OnClickListener,
        DialogInterface.OnClickListener,
        SelectDialog.OnItemClickListener,
        DatePickerDialog.OnDateSetListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.fab_add)
    FloatingActionButton fabAdd;
    @Bind(R.id.nsv_content)
    NestedScrollView nsvContent;
    @Bind(R.id.ll_content)
    LinearLayout llContent;

    private SystemBarTintManager tintManager;

    private Intent intent;
    private String from;    //标识当前添加的条目是日记or便笺
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private SelectDialog weatherDialog;
    private SelectDialog bgDialog;
    private int weather = 0;        //记录该条目的天气
    private int background = 0;     //条目的背景

    private int year = Mojian.year;
    private int month = Mojian.month;
    private int day = Mojian.day;

    private int[] weathers = {
            R.drawable.weather_sun,
            R.drawable.weather_clouds,
            R.drawable.weather_clouds_with_rain,
            R.drawable.weather_clouds_with_snow
    };

    private final int SELECT_IMAGE = 0;
    private final int PER_EXTERNAL_STORAGE = 0;

    private int imgCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initStatusBar(background);
        setTheme(Mojian.themeIds[background]);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);

        initView();
        setListener();
    }

    //设置透明状态栏
    private void initStatusBar(int background) {
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        if (android.os.Build.MANUFACTURER.toLowerCase().equals("huawei")) {
            tintManager.setStatusBarTintColor(Mojian.darkColors[background]);
        } else {
            tintManager.setStatusBarTintColor(Mojian.colors[background]);
        }
    }

    private void initView() {
        intent = getIntent();
        from = intent.getStringExtra("from");
        databaseHelper = new DatabaseHelper(this, "Mojian.db", null, 2);
        database = databaseHelper.getWritableDatabase();

        setSupportActionBar(toolbar);
        if (from.equals("note")) {
            toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_black_24dp);
        }
        ActionBar actionBar = getSupportActionBar();
        nsvContent.setBackgroundResource(Mojian.backgroundIds[background]);
        llContent.setBackgroundResource(Mojian.backgroundIds[background]);

        if (actionBar != null) {
            if (from.equals("diary")) {
                actionBar.setTitle(getString(R.string.tt_add_diary));
            } else {
                actionBar.setTitle(getString(R.string.tt_add_note));
            }
        }
        //显示软键盘
        etContent.requestFocus();
        SoftInput.show(etContent);
    }

    private void setListener() {
        toolbar.setNavigationOnClickListener(this);
        fabAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_add) {        //添加图片
            if (PermissionUtil.checkA(this, Manifest.permission.READ_EXTERNAL_STORAGE, PER_EXTERNAL_STORAGE)) {
                if (imgCount < 5) {         //最多插入5张图片
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_IMAGE);
                } else {
                    Snackbar.make(toolbar, getString(R.string.toast_image_overflow), Snackbar.LENGTH_SHORT).show();
                }
            }
        } else {                                //ToolBar保存
            ContentValues values = new ContentValues();
            if (etContent.getText().length() == 0) {
                if (from.equals("diary")) {
                    SoftInput.hide(etContent);
                    Snackbar.make(etContent, "内容不能为空！", Snackbar.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            } else {
                values.put("year", year);
                values.put("month", month);
                values.put("day", day);
                values.put("content", etContent.getText().toString());
                values.put("background", background);
                if (from.equals("diary")) {
                    values.put("weather", weather);
                    database.insert("diary", null, values);
                } else {
                    database.insert("note", null, values);
                }
                SoftInput.hide(etContent);
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PER_EXTERNAL_STORAGE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (imgCount < 5) {         //最多插入5张图片
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_IMAGE);
            } else {
                Snackbar.make(toolbar, getString(R.string.toast_image_overflow), Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(toolbar, getString(R.string.toast_per_fail), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            ContentValues values = new ContentValues();
            values.put("year", year);
            values.put("month", month);
            values.put("day", day);
            values.put("content", etContent.getText().toString());
            values.put("background", background);
            values.put("weather", weather);
            database.insert("diary", null, values);
        }
        finish();
    }

    @Override
    public void onItemClick(SelectDialog dialog, int position) {
        if (dialog == weatherDialog) {
            weather = position;
            invalidateOptionsMenu();    //更新menu
        } else {
            background = position;
            //设置背景纸张
            nsvContent.setBackgroundResource(Mojian.backgroundIds[background]);
            llContent.setBackgroundResource(Mojian.backgroundIds[background]);
            //设置StatusBar&ToolBar颜色
            if (android.os.Build.MANUFACTURER.toLowerCase().equals("huawei")) {
                tintManager.setStatusBarTintColor(Mojian.darkColors[background]);
            } else {
                tintManager.setStatusBarTintColor(Mojian.colors[background]);
            }
            toolbar.setBackgroundColor(Mojian.colors[background]);
        }
        dialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap;
                    try {   //Bitmap压缩
                        bitmap = BitmapUtil.compress(this, getContentResolver().openInputStream(data.getData()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                    long currentTimeMill = BitmapUtil.save(bitmap, getString(R.string.path_pic), 40);   //保存至本地
                    AlignImageSpan imageSpan = new AlignImageSpan(this, bitmap, AlignImageSpan.ALIGN_CENTER);
                    String tag = "<"
                            + Environment.getExternalStorageDirectory() + getString(R.string.path_pic) + currentTimeMill
                            + ".jpg>\n";
                    SpannableString spannableString = new SpannableString(tag);
                    spannableString.setSpan(imageSpan, 0, tag.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    int index = etContent.getSelectionStart();
                    Editable editable = etContent.getEditableText();
                    if (index < 0 || index >= editable.length()) {
                        editable.append(spannableString);
                    } else {
                        editable.insert(index + 1, spannableString);
                    }
                    imgCount ++;    //标识当前插入的图片数量
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        if (from.equals("note")) {
            menu.findItem(R.id.action_weather).setVisible(false);
            menu.findItem(R.id.action_date).setVisible(false);
        } else {
            menu.findItem(R.id.action_weather).setIcon(weathers[weather]);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_weather:
                weatherDialog = new SelectDialog(this, R.style.Widget_SelectDialog, R.layout.dialog_weather);
                Window weatherWindow = weatherDialog.getWindow();
                weatherWindow.setGravity(Gravity.TOP | Gravity.RIGHT);
                weatherDialog.show();
                weatherDialog.setOnItemClickListener(weatherDialog, this);
                break;
            case R.id.action_background:
                bgDialog = new SelectDialog(this, R.style.Widget_SelectDialog, R.layout.dialog_background);
                Window bgWindow = bgDialog.getWindow();
                bgWindow.setGravity(Gravity.TOP | Gravity.RIGHT);
                bgDialog.show();
                bgDialog.setOnItemClickListener(bgDialog, this);
                break;
            case R.id.action_date:
                new DatePickerDialog(
                        this,
                        this,
                        year,
                        month,
                        day
                ).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }

    @Override
    public void onBackPressed() {
        //记便笺时可以退出保存，写日记时需要提示是否保存
        if (!etContent.getText().toString().equals("")) {
            if (from.equals("note")) {
                ContentValues values = new ContentValues();
                values.put("year", Mojian.year);
                values.put("month", Mojian.month);
                values.put("day", Mojian.day);
                values.put("content", etContent.getText().toString());
                values.put("background", background);
                database.insert("note", null, values);
                super.onBackPressed();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("保存")
                        .setMessage("需要保存该日记吗？")
                        .setPositiveButton("保存", this)
                        .setNegativeButton("不保存", this)
                        .setCancelable(false)
                        .show();
            }
        } else {
            super.onBackPressed();
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
}
