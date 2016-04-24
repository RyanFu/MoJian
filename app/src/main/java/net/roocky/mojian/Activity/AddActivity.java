package net.roocky.mojian.Activity;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import net.roocky.mojian.Database.DatabaseHelper;
import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;
import net.roocky.mojian.Util.SDKVersion;
import net.roocky.mojian.Util.ScreenUtil;
import net.roocky.mojian.Util.SoftInput;
import net.roocky.mojian.Widget.SelectDialog;

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
        ViewTreeObserver.OnGlobalLayoutListener{
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.cl_main)
    CoordinatorLayout clMain;
    @Bind(R.id.iv_bottom)
    ImageView ivBottom;
    @Bind(R.id.iv_background)
    ImageView ivBackground;

    private Intent intent;
    private String from;    //标识当前添加的条目是日记or便笺
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private SelectDialog weatherDialog;
    private SelectDialog bgDialog;
    private int weather = 0;        //记录该条目的天气
    private int background;     //条目的背景

    private int[] weathers = {
            R.drawable.weather_sun,
            R.drawable.weather_clouds,
            R.drawable.weather_clouds_with_rain,
            R.drawable.weather_clouds_with_snow
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);

        initView();
        setOnClickListener();
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

    private void setOnClickListener() {
        toolbar.setNavigationOnClickListener(this);
        clMain.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /**
     * 只有Toolbar的NavigationIcon绑定了点击事件，所以这里就没有进行id判断
     * @param v
     */
    @Override
    public void onClick(View v) {
        ContentValues values = new ContentValues();
        if (etContent.getText().length() == 0) {
            if (from.equals("diary")) {
                SoftInput.hide(etContent);
                Snackbar.make(etContent, "内容不能为空！", Snackbar.LENGTH_SHORT).show();
            } else {
                finish();
            }
        } else {
            values.put("year", Mojian.year);
            values.put("month", Mojian.month);
            values.put("day", Mojian.day);
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

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            ContentValues values = new ContentValues();
            values.put("year", Mojian.year);
            values.put("month", Mojian.month);
            values.put("day", Mojian.day);
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
            ivBackground.setImageResource(Mojian.backgrounds[background]);
            ivBottom.setImageResource(Mojian.backgrounds[background]);
        }
        dialog.dismiss();
    }

    //界面view变化监听
    @Override
    public void onGlobalLayout() {
        //根据软键盘是否显示决定背景图片的位置
        if (ScreenUtil.isSoftInputShow(clMain)) {
            ivBackground.setVisibility(View.GONE);
            ivBottom.setVisibility(View.VISIBLE);
        } else {
            ivBackground.setVisibility(View.VISIBLE);
            ivBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        if (from.equals("note")) {
            menu.getItem(0).setVisible(false);
        } else {
            menu.getItem(0).setIcon(weathers[weather]);
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
        }
        return super.onOptionsItemSelected(item);
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
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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
