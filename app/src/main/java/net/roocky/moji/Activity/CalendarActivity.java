package net.roocky.moji.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import net.roocky.moji.Adapter.BaseAdapter;
import net.roocky.moji.Decorator.CalendarDecorator;
import net.roocky.moji.Adapter.DiaryAdapter;
import net.roocky.moji.Moji;
import net.roocky.moji.R;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by roocky on 04/06.
 * 根据日期查看日记
 */
public class CalendarActivity extends AppCompatActivity implements
        OnDateSelectedListener,
        BaseAdapter.OnItemClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.cld_diary)
    MaterialCalendarView cldDiary;
    @Bind(R.id.rv_diary)
    RecyclerView rvDiary;

    private DiaryAdapter adapter;

    //日历中当前所选择的日期
    private int year = Moji.year;
    private int month = Moji.month;
    private int day = Moji.day;

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

        //设置RecyclerView
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new DiaryAdapter(this,
                new String[]{"id", "year", "month", "day", "content"},
                "year=? and month=? and day=?",
                new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)});
        rvDiary.setLayoutManager(manager);
        rvDiary.setAdapter(adapter);

        //设置CalendarView的当前日期
        cldDiary.setSelectedDate(Calendar.getInstance());

        //向Decorator传入一个包含所有记了日记的CalendarDay的list
        List<CalendarDay> dayList = adapter.getDayList();
        cldDiary.addDecorator(new CalendarDecorator(dayList));
    }

    //绑定点击监听事件
    private void setOnClickListener() {
        cldDiary.setOnDateChangedListener(this);     //日历
        adapter.setOnItemClickListener(this);       //RecyclerView
        adapter.setOnItemLongClickListener(new BaseAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }

    //日历选择监听事件
    //刷新RecyclerView & 判断当天是否有日记
    @Override
    public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
        flush(date);
    }

    //RecyclerView刷新&设置日期
    private void flush(CalendarDay date) {
        this.year = date.getYear();
        this.month = date.getMonth();
        this.day = date.getDay();
        adapter.listRefresh("diary",
                new String[]{"id", "year", "month", "day", "content"},
                "year=? and month=? and day=?",
                new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)});
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() == 0) {
            Snackbar.make(cldDiary, getString(R.string.toast_null), Snackbar.LENGTH_SHORT).show();
        }
    }

    //Recycler子项点击
    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra("id", (view.findViewById(R.id.cv_item)).getTag(R.id.tag_id).toString());     //id作为删除和修改的标识
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
        flush(CalendarDay.from(year, month, day));        //当在CalendarActivity中完成日记的删除or编辑操作后需要刷新日记列表
    }
}
