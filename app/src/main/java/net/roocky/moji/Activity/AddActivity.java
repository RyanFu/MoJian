package net.roocky.moji.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;

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
public class AddActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_content)
    EditText etContent;

    private Intent intent;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

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
                database.insert("diary", null, values);
            } else {
                database.insert("note", null, values);
            }
            SoftInput.hide(etContent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        //记便笺时可以退出保存，写日记时需要手动保存
        if (intent.getStringExtra("from").equals("note") && !etContent.getText().toString().equals("")) {
            ContentValues values = new ContentValues();
            values.put("year", Moji.year);
            values.put("month", Moji.month);
            values.put("day", Moji.day);
            values.put("content", etContent.getText().toString());
            database.insert("note", null, values);
        }
        super.onBackPressed();
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
