package net.roocky.mojian.Activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.soundcloud.android.crop.Crop;
import com.umeng.analytics.MobclickAgent;

import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;
import net.roocky.mojian.Util.SoftInput;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by roocky on 03/18.
 * 账号信息
 */
public class AccountActivity extends BaseActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener{
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.sdv_avatar)
    SimpleDraweeView sdvAvatar;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.tv_sex)
    TextView tvSex;
    @Bind(R.id.tv_birthday)
    TextView tvBirthday;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_signature)
    TextView tvSignature;
    @Bind(R.id.ll_nickname)
    LinearLayout llNickname;
    @Bind(R.id.ll_sex)
    LinearLayout llSex;
    @Bind(R.id.ll_birthday)
    LinearLayout llBirthday;
    @Bind(R.id.ll_address)
    LinearLayout llAddress;
    @Bind(R.id.ll_signature)
    LinearLayout llSignature;

    private AlertDialog dialogNickname;
    private AlertDialog dialogSex;
    private AlertDialog dialogAddress;
    private AlertDialog dialogSignature;

    private EditText etNickname;
    private String[] sexs = new String[]{"男", "女"};
    private EditText etAddress;
    private EditText etSignature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);

        initView();
        setOnClickListener();
    }

    private void initView() {
        curActivity = ACTIVITY_ACCOUNT;
        String uriAvatar = preferences.getString("avatar", null);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.set_account));
        }
        if (uriAvatar != null) {
            sdvAvatar.setImageURI(Uri.parse(uriAvatar));
        }
    }

    private void setOnClickListener() {
        sdvAvatar.setOnClickListener(this);
        llNickname.setOnClickListener(this);
        llSex.setOnClickListener(this);
        llBirthday.setOnClickListener(this);
        llAddress.setOnClickListener(this);
        llSignature.setOnClickListener(this);
    }

    @Override
    protected void setAvatar(Uri imageUri) {
        sdvAvatar.setImageURI(imageUri);
    }

    /**
     * View点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sdv_avatar:
                dialogAvatar = new AlertDialog.Builder(this)
                        .setTitle("更改头像")
                        .setItems(new String[]{"拍照", "从相册中选中"}, this)
                        .show();
                break;
            case R.id.ll_nickname:
                dialogNickname = new AlertDialog.Builder(this)
                        .setTitle("昵称")
                        .setView(etNickname = new EditText(this))
                        .setPositiveButton("确定", this)
                        .setNegativeButton("取消", null)
                        .show();
                etNickname.setText(preferences.getString("nickname", ""));
                SoftInput.show(etNickname);     //自动打开软键盘
                etNickname.requestFocus();
                break;
            case R.id.ll_sex:
                dialogSex = new AlertDialog.Builder(this)
                        .setTitle("性别")
                        .setSingleChoiceItems(sexs, preferences.getInt("sex", 0), this)
                        .show();
                break;
            case R.id.ll_birthday:
                int year = preferences.getInt("birthdayYear", Mojian.year);
                int month = preferences.getInt("birthdayMonth", Mojian.month);
                int day = preferences.getInt("birthdayDay", Mojian.day);
                new DatePickerDialog(
                        this,
                        this,
                        year,
                        month,
                        day).show();
                break;
            case R.id.ll_address:
                dialogAddress = new AlertDialog.Builder(this)
                        .setTitle("地址")
                        .setView(etAddress = new EditText(this))
                        .setPositiveButton("确定", this)
                        .setNegativeButton("取消", null)
                        .show();
                etAddress.setText(preferences.getString("address", ""));
                SoftInput.show(etAddress);     //自动打开软键盘
                etAddress.requestFocus();
                break;
            case R.id.ll_signature:
                dialogSignature = new AlertDialog.Builder(this)
                        .setTitle("个性签名")
                        .setView(etSignature = new EditText(this))
                        .setPositiveButton("确定", this)
                        .setNegativeButton("取消", null)
                        .show();
                etSignature.setText(preferences.getString("signature", ""));
                SoftInput.show(etSignature);     //自动打开软键盘
                etSignature.requestFocus();
                break;
            default:
                break;
        }
    }

    /**
     * 弹窗点击事件
     *
     * @param dialog
     * @param which
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);           //更改头像&背景图片
        if (dialog.equals(dialogNickname)) {
            tvNickname.setText(etNickname.getText());
            editor.putString("nickname", etNickname.getText().toString()).commit();
        } else if (dialog.equals(dialogSex)) {
            tvSex.setText(sexs[which]);
            editor.putInt("sex", which).commit();
            dialog.dismiss();
        } else if (dialog.equals(dialogAddress)) {
            tvAddress.setText(etAddress.getText());
            editor.putString("address", etAddress.getText().toString()).commit();
        } else if (dialog.equals(dialogSignature)) {
            tvSignature.setText(etSignature.getText());
            editor.putString("signature", etSignature.getText().toString()).commit();
        }
    }

    //日期选择器监听事件
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        editor.putInt("birthdayYear", year);
        editor.putInt("birthdayMonth", monthOfYear);    //月份从0开始
        editor.putInt("birthdayDay", dayOfMonth);
        editor.commit();
        String birthday = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        tvBirthday.setText(birthday);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvNickname.setText(preferences.getString("nickname", ""));
        tvSex.setText(sexs[preferences.getInt("sex", 0)]);
        String birthday = preferences.getInt("birthdayYear", Mojian.year) + "-" +
                (preferences.getInt("birthdayMonth", Mojian.month) + 1) + "-" +
                preferences.getInt("birthdayDay", Mojian.day);
        tvBirthday.setText(birthday);
        tvAddress.setText(preferences.getString("address", ""));
        tvSignature.setText(preferences.getString("signature", ""));
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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

    //MainActivity中设置背景图片
    @Override
    protected void setBackground(Uri imageUri) {}
}
