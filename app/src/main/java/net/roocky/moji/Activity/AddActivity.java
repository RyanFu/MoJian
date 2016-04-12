package net.roocky.moji.Activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;

import net.roocky.moji.Adapter.WeatherAdapter;
import net.roocky.moji.Database.DatabaseHelper;
import net.roocky.moji.Moji;
import net.roocky.moji.R;
import net.roocky.moji.Util.SoftInput;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by roocky on 03/28.
 * 新建
 */
public class AddActivity extends AppCompatActivity implements
        View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        DialogInterface.OnClickListener{
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.spn_weather)
    AppCompatSpinner spnWeather;
    @Bind(R.id.rl_toolbar)
    RelativeLayout rlToolbar;

    private Intent intent;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private int weather;

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
        databaseHelper = new DatabaseHelper(this, "Moji.db", null, 1);
        database = databaseHelper.getWritableDatabase();

        setSupportActionBar(toolbar);
        if (intent.getStringExtra("from").equals("note")) {
            toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_black_24dp);
        }
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            if (intent.getStringExtra("from").equals("diary")) {
//                actionBar.setTitle(getString(R.string.tt_add_diary));     //rlToolbar中已经设置了"title"
                rlToolbar.setVisibility(View.VISIBLE);
            } else {
                actionBar.setTitle(getString(R.string.tt_add_note));
            }
        }
        //显示软键盘
        etContent.requestFocus();
        SoftInput.show(etContent);
        //配置Spinner
        spnWeather.setAdapter(new WeatherAdapter(this));
        spnWeather.setOnItemSelectedListener(this);
    }

    private void setOnClickListener() {
        toolbar.setNavigationOnClickListener(this);
    }

    /**
     * 只有Toolbar的NavigationIcon绑定了点击事件，所以这里就没有进行id判断
     * @param v
     */
    @Override
    public void onClick(View v) {
        ContentValues values = new ContentValues();
        if (etContent.getText().length() == 0) {
            if (intent.getStringExtra("from").equals("diary")) {
                SoftInput.hide(etContent);
                Snackbar.make(etContent, "内容不能为空！", Snackbar.LENGTH_SHORT).show();
            } else {
                finish();
            }
        } else {
            values.put("year", Moji.year);
            values.put("month", Moji.month);
            values.put("day", Moji.day);
            values.put("content", etContent.getText().toString());
            if (intent.getStringExtra("from").equals("diary")) {
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        weather = position;
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            ContentValues values = new ContentValues();
            values.put("year", Moji.year);
            values.put("month", Moji.month);
            values.put("day", Moji.day);
            values.put("content", etContent.getText().toString());
            values.put("weather", weather);
            database.insert("diary", null, values);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        //记便笺时可以退出保存，写日记时需要提示是否保存
        if (!etContent.getText().toString().equals("")) {
            if (intent.getStringExtra("from").equals("note")) {
                ContentValues values = new ContentValues();
                values.put("year", Moji.year);
                values.put("month", Moji.month);
                values.put("day", Moji.day);
                values.put("content", etContent.getText().toString());
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
}
