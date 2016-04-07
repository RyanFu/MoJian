package net.roocky.moji.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import net.roocky.moji.Adapter.BaseAdapter;
import net.roocky.moji.Adapter.DiaryAdapter;
import net.roocky.moji.Database.DatabaseHelper;
import net.roocky.moji.Model.Diary;
import net.roocky.moji.Moji;
import net.roocky.moji.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by roocky on 04/06.
 * 根据日期查看日记
 */
public class CalendarActivity extends AppCompatActivity implements
        CalendarView.OnDateChangeListener,
        BaseAdapter.OnItemClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.cld_diary)
    CalendarView cldDiary;
    @Bind(R.id.rv_diary)
    RecyclerView rvDiary;

    private DiaryAdapter adapter;

    private int year, month, day;   //日历中当前所选择的日期

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);

        initView();
        setOnClickListener();
    }

    //初始化view
    private void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.action_calendar));
        }

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new DiaryAdapter(this);
        rvDiary.setLayoutManager(manager);
        rvDiary.setAdapter(adapter);

        flush(Moji.year, Moji.month, Moji.day);
    }

    //绑定点击监听事件
    private void setOnClickListener() {
        cldDiary.setOnDateChangeListener(this);     //日历
        adapter.setOnItemClickListener(this);       //RecyclerView
        adapter.setOnItemLongClickListener(new BaseAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }

    //日历选择监听事件
    @Override
    public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
        flush(year, month, day);
    }

    private void flush(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        adapter.listRefresh("diary",
                new String[]{"id", "year", "month", "day", "content"},
                "year=? and month=? and day=?",
                new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)});
        adapter.notifyDataSetChanged();
    }

    //Recycler子项点击
    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra("id", (view.findViewById(R.id.cv_item)).getTag().toString());     //id作为删除和修改的标识
        intent.putExtra("content", ((TextView) (view.findViewById(R.id.tv_content))).getText().toString());
        if (view.findViewById(R.id.tv_remind) != null) {    //判断当前的Fragment是diary还是note
            intent.putExtra("from", "note");
            intent.putExtra("remind", ((TextView) (view.findViewById(R.id.tv_remind))).getText().toString());
        } else {
            intent.putExtra("from", "diary");
        }
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flush(year, month, day);
    }
}
