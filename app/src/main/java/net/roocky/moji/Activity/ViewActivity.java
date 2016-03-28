package net.roocky.moji.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.roocky.moji.Database.DatabaseHelper;
import net.roocky.moji.R;
import net.roocky.moji.Util.SoftInput;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by roocky on 03/28.
 * 查看内容
 */
public class ViewActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.fab_edit)
    FloatingActionButton fabEdit;

    private Intent intent;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private boolean isEdit = false;     //标识当前是否为编辑状态

    private AlertDialog dialogDiary;
    private AlertDialog dialogNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);

        initView();
        setOnClickListener();
    }

    private void initView() {
        intent = getIntent();
        databaseHelper = new DatabaseHelper(this, "Moji.db", null, 1);
        database = databaseHelper.getWritableDatabase();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.app_name));
        }
        //显示内容
        tvContent.setText(intent.getStringExtra("content"));
    }

    private void setOnClickListener() {
        fabEdit.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    //ActionBar菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:     //返回箭头
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
                toolbar.setNavigationIcon(R.mipmap.ic_done_black_24dp);
                toolbar.setNavigationOnClickListener(this);

                tvContent.setVisibility(View.GONE);
                etContent.setText(tvContent.getText());
                etContent.setVisibility(View.VISIBLE);
                etContent.requestFocus();
                fabEdit.hide();
                SoftInput.show(etContent);  //显示软键盘

                isEdit = true;
                break;
            default:
                if (isEdit) {       //保存点击事件
                    ContentValues values = new ContentValues();
                    values.put("content", etContent.getText().toString());
                    if (intent.getStringExtra("from").equals("diary")) {
                        database.update("diary", values, "content = ?", new String[]{tvContent.getText().toString()});
                    } else {
                        database.update("note", values, "content = ?", new String[]{tvContent.getText().toString()});
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
            database.delete("diary", "content = ?", new String[]{tvContent.getText().toString()});
        } else {                            //删除便笺
            database.delete("note", "content = ?", new String[]{tvContent.getText().toString()});
        }
        finish();
    }
}
