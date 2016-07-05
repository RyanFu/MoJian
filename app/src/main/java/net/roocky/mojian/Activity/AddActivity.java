package net.roocky.mojian.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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
import android.text.TextWatcher;
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

import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.roocky.mojian.Database.DatabaseHelper;
import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;
import net.roocky.mojian.Util.BitmapUtil;
import net.roocky.mojian.Util.PermissionUtil;
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
        SelectDialog.OnItemClickListener,
        DatePickerDialog.OnDateSetListener,
        ViewTreeObserver.OnGlobalLayoutListener,
        NestedScrollView.OnScrollChangeListener,
        TextWatcher{
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.fab_add)
    FloatingActionButton fabAdd;
    @Bind(R.id.nsv_content)
    NestedScrollView nsvContent;
    @Bind(R.id.iv_background)
    ImageView ivBackground;
    @Bind(R.id.iv_bottom)
    ImageView ivBottom;
    @Bind(R.id.cl_main)
    CoordinatorLayout clMain;

    private SystemBarTintManager tintManager;

    private Intent intent;
    private String from;    //标识当前添加的条目是日记or便笺
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private SelectDialog sdWeather;
    private SelectDialog sdPaper;
    private SelectDialog sdBackground;
    private int weather = 0;        //记录该条目的天气
    private int paper = 0;     //条目的纸张颜色
    private int background = 0; //背景图片

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

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initStatusBar();
        setTheme(Mojian.themeIds[paper]);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);

        intent = getIntent();
        if (intent.getStringExtra("from") == null) {
            handleShare();      //接收其他应用的文本分享
            intent.putExtra("from", "note");
        }

        initView();
        setListener();
    }

    //设置透明状态栏
    private void initStatusBar() {
        preferences = getSharedPreferences("mojian", MODE_PRIVATE);
        editor = preferences.edit();
        paper = preferences.getInt("defaultPaper", 0);
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        if (android.os.Build.MANUFACTURER.toLowerCase().equals("huawei")) {
            tintManager.setStatusBarTintColor(Mojian.darkColors[paper]);
        } else {
            tintManager.setStatusBarTintColor(Mojian.colors[paper]);
        }
    }

    private void initView() {
        from = intent.getStringExtra("from");
        databaseHelper = new DatabaseHelper(this, "Mojian.db", null, 3);
        database = databaseHelper.getWritableDatabase();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        nsvContent.setBackgroundColor(Mojian.colors[paper]);

        if (actionBar != null) {
            if (from.equals("diary")) {
                actionBar.setTitle(getString(R.string.tt_add_diary));
            } else {
                actionBar.setTitle(getString(R.string.tt_add_note));
            }
        }
        etContent.setTextSize(Mojian.fontSize[preferences.getInt("fontSize", 1)]);
        //显示软键盘
        etContent.requestFocus();
        SoftInput.show(etContent);
        //设置默认背景图
        background = preferences.getInt("defaultBackground", 0);
        ivBackground.setImageResource(Mojian.backgrounds[background]);
        ivBottom.setImageResource(Mojian.backgrounds[background]);
        //恢复草稿
        if (!preferences.getString("tempNote", "").equals("") && from.equals("note")) {
            etContent.setText(preferences.getString("tempNote", ""));
        } else if (!preferences.getString("tempDiary", "").equals("") && from.equals("diary")){
            etContent.setText(preferences.getString("tempDiary", ""));
        }
    }

    private void setListener() {
        toolbar.setNavigationOnClickListener(this);
        nsvContent.setOnScrollChangeListener(this);
        fabAdd.setOnClickListener(this);
        clMain.getViewTreeObserver().addOnGlobalLayoutListener(this);
        etContent.addTextChangedListener(this);
    }

    private void handleShare() {
        if (intent.getAction().equals(Intent.ACTION_SEND) && intent.getType().equals("text/plain")) {
            String shareText = intent.getStringExtra(Intent.EXTRA_TEXT);
            String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            if (shareText != null) {
                etContent.setText(shareText);
            }
        }
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
                finish();
            } else {
                values.put("year", year);
                values.put("month", month);
                values.put("day", day);
                values.put("content", etContent.getText().toString());
                values.put("background", background);
                values.put("paper", paper);
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
    public void onItemClick(SelectDialog dialog, int position) {
        if (dialog == sdWeather) {
            weather = position;
            invalidateOptionsMenu();    //更新menu
        } else if (dialog == sdPaper) {
            paper = position;
            editor.putInt("defaultPaper", paper).apply();
            //设置背景纸张
            nsvContent.setBackgroundColor(Mojian.colors[paper]);
            //设置StatusBar&ToolBar颜色
            if (android.os.Build.MANUFACTURER.toLowerCase().equals("huawei")) {
                tintManager.setStatusBarTintColor(Mojian.darkColors[paper]);
            } else {
                tintManager.setStatusBarTintColor(Mojian.colors[paper]);
            }
            toolbar.setBackgroundColor(Mojian.colors[paper]);
        } else if (dialog == sdBackground) {
            background = position;
            editor.putInt("defaultBackground", background).apply();
            if (background == 0) {  //背景图片选择了null
                ivBackground.setVisibility(View.GONE);
                ivBottom.setVisibility(View.GONE);
            } else {                //背景图片选择了花
                ivBackground.setImageResource(Mojian.backgrounds[background]);
                ivBottom.setImageResource(Mojian.backgrounds[background]);
            }
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
                    long currentTimeMill = BitmapUtil.save(bitmap, getString(R.string.path_cache), 40);   //保存至本地
                    AlignImageSpan imageSpan = new AlignImageSpan(this, bitmap, AlignImageSpan.ALIGN_CENTER);
                    String tag = "<"
                            + Environment.getExternalStorageDirectory() + getString(R.string.path_cache) + currentTimeMill
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
        Window window;
        switch (item.getItemId()) {
            case R.id.action_weather:
                sdWeather = new SelectDialog(this, R.style.Widget_SelectDialog, R.layout.dialog_weather);
                window = sdWeather.getWindow();
                window.setGravity(Gravity.TOP | Gravity.RIGHT);
                sdWeather.show();
                sdWeather.setOnItemClickListener(sdWeather, this);
                break;
            case R.id.action_paper:     //纸张颜色
                sdPaper = new SelectDialog(this, R.style.Widget_SelectDialog, R.layout.dialog_paper);
                window = sdPaper.getWindow();
                window.setGravity(Gravity.TOP | Gravity.RIGHT);
                sdPaper.show();
                sdPaper.setOnItemClickListener(sdPaper, this);
                break;
            case R.id.action_background:        //底部背景图片
                sdBackground = new SelectDialog(this, R.style.Widget_SelectDialog, R.layout.dialog_background);
                window = sdBackground.getWindow();
                window.setGravity(Gravity.TOP | Gravity.RIGHT);
                sdBackground.show();
                sdBackground.setOnItemClickListener(sdBackground, this);
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
    public void onGlobalLayout() {
        //根据软键盘是否显示决定背景图片的位置
        if (ScreenUtil.isSoftInputShow(clMain)) {
            ivBackground.setVisibility(View.GONE);
            ivBottom.setVisibility(View.VISIBLE);
        } else {
            if (toolbar.getMeasuredHeight() + etContent.getMeasuredHeight()
                    + ivBackground.getMeasuredHeight() > ScreenUtil.getHeight(this) - 200) {//如果TextView过长需要隐藏background显示Bottom
                ivBackground.setVisibility(View.GONE);
                ivBottom.setVisibility(View.VISIBLE);
            } else {
                ivBackground.setVisibility(View.VISIBLE);
                ivBottom.setVisibility(View.GONE);
            }
        }
    }

    //滚动监听（隐藏 or 显示FAB）
    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (scrollY - oldScrollY > 0) {
            fabAdd.hide();
        } else {
            fabAdd.show();
        }
    }

    @Override
    public void onBackPressed() {
        //记便笺时可以退出保存，写日记时需要提示是否保存
        if (!etContent.getText().toString().equals("")) {
            ContentValues values = new ContentValues();
            values.put("year", year);
            values.put("month", month);
            values.put("day", day);
            values.put("content", etContent.getText().toString());
            values.put("background", background);
            values.put("paper", paper);
            if (from.equals("note")) {
                database.insert("note", null, values);
                editor.putString("tempNote", "").apply();
            } else {
                values.put("weather", weather);
                database.insert("diary", null, values);
                editor.putString("tempDiary", "").apply();
            }
        }
        super.onBackPressed();
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
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            clMain.getViewTreeObserver().removeOnGlobalLayoutListener(this);        //防止内存泄漏
        } else {
            clMain.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (from.equals("note")) {
            editor.putString("tempNote", s.toString()).apply();
        } else {
            editor.putString("tempDiary", s.toString()).apply();
        }
    }
}
