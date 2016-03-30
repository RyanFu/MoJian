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

import net.roocky.moji.Database.DatabaseHelper;
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

    private String[] numbers;

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
        numbers = getResources().getStringArray(R.array.number_array);

        setSupportActionBar(toolbar);
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
//            int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        ContentValues values = new ContentValues();
        if (etContent.getText().length() == 0) {
            SoftInput.hide(etContent);
            Snackbar.make(etContent, "内容不能为空！", Snackbar.LENGTH_SHORT).show();
        } else {
            if (intent.getStringExtra("from").equals("diary")) {
                //需要判断长度是否为“1”，若不为“1”则需要加“\n”
                String strMonth = (numbers[month].length() == 1 ? numbers[month] : new StringBuilder(numbers[month]).insert(1, "\n")).toString();
                String strDay = (numbers[day - 1].length() == 1 ? numbers[day - 1] : new StringBuilder(numbers[day - 1]).insert(1, "\n")).toString();
                values.put("date", strMonth + "\n · \n" + strDay);
                values.put("content", etContent.getText().toString());
                database.insert("diary", null, values);
            } else {
                values.put("date", numbers[month] + " · " + numbers[day - 1]);
                values.put("content", etContent.getText().toString());
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
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            ContentValues values = new ContentValues();
            values.put("date", numbers[month] + " · " + numbers[day - 1]);
            values.put("content", etContent.getText().toString());
            database.insert("note", null, values);
        }
        super.onBackPressed();
    }
}
