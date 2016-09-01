package net.roocky.mojian.APPWidgetConfigure;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RemoteViews;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.roocky.mojian.Fragment.NoteFragment;
import net.roocky.mojian.Fragment.WidgetNoteFragment;
import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Roocky on 2016/8/20 0020.
 * 小部件配置（选择笔记）
 */
public class ItemAppWidgetConfigure extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private SystemBarTintManager tintManager;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private WidgetNoteFragment widgetNoteFragment = new WidgetNoteFragment();

    private static int appWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_item_appwidget);
        ButterKnife.bind(this);

        initView();
    }

    //设置透明状态栏
    private void initStatusBar() {
        if (Mojian.devices.contains(android.os.Build.MANUFACTURER.toLowerCase())) {
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.grey_600);
        }
    }

    //初始化view
    private void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.configure_choose));
        }
        fragmentManager.beginTransaction().replace(R.id.fl_content, widgetNoteFragment).commit();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //获取小部件的ID
    public static int getAppWidgetId() {
        return appWidgetId;
    }
}
