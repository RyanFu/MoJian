package net.roocky.mojian.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.roocky.mojian.Activity.PatternConfirmActivity;
import net.roocky.mojian.Activity.PatternSetActivity;
import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;
import net.roocky.mojian.Util.DeviceJudge;
import net.roocky.mojian.Util.FileUtil;
import net.roocky.mojian.Util.PermissionUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by roocky on 03/16.
 * 设置Fragment
 */
public class SettingFragment extends Fragment implements View.OnClickListener,
        DialogInterface.OnClickListener {
    @Bind(R.id.ll_size)
    LinearLayout llSize;
    @Bind(R.id.tv_size_detail)
    TextView tvSizeDetail;
    @Bind(R.id.ll_drawer)
    LinearLayout llDrawer;
    @Bind(R.id.tv_drawer_detail)
    TextView tvDrawerDetail;
    @Bind(R.id.ll_backup)
    LinearLayout llBackup;
    @Bind(R.id.tv_backup_detail)
    TextView tvBackupDetail;
    @Bind(R.id.ll_restore)
    LinearLayout llRestore;
    @Bind(R.id.ll_lock)
    LinearLayout llLock;
    @Bind(R.id.ll_clear_lock)
    LinearLayout llClearLock;
    @Bind(R.id.ll_feedback)
    LinearLayout llFeedback;
    @Bind(R.id.ll_about)
    LinearLayout llAbout;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private final int PER_EXTERNAL_STORAGE = 0;
    private int idClick;
    private final int PATTERN_LOCK_SET = 4;
    private final int PATTERN_LOCK_CLEAR = 5;
    private final int SELECT_FILE = 6;

    private final int RESULT_OK = -1;

    private AlertDialog adBackup;
    private AlertDialog adSize;
    private AlertDialog adDrawer;
    
    private String[] fontSize = {"大", "中", "小"};
    private String[] drawerPosition = {"左", "右"};
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);

        preferences = getActivity().getSharedPreferences("mojian", getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        setOnClickListener();

        return view;
    }

    private void setOnClickListener() {
        llSize.setOnClickListener(this);
        llDrawer.setOnClickListener(this);
        llBackup.setOnClickListener(this);
        llRestore.setOnClickListener(this);
        llLock.setOnClickListener(this);
        llClearLock.setOnClickListener(this);
        llFeedback.setOnClickListener(this);
        llAbout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String patternSha1 = preferences.getString("patternSha1", "");
        switch (v.getId()) {
            case R.id.ll_drawer:
                adDrawer = new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.set_drawer))
                        .setSingleChoiceItems(drawerPosition, preferences.getInt("drawerPosition", 0), this)
                        .show();
                break;
            case R.id.ll_size:
                adSize = new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.set_size))
                        .setSingleChoiceItems(fontSize, preferences.getInt("fontSize", 1), this)
                        .show();
                break;
            case R.id.ll_lock:
                if (patternSha1.equals("")) {
                    startActivity(new Intent(getActivity(), PatternSetActivity.class));
                } else {
                    startActivityForResult(new Intent(getActivity(), PatternConfirmActivity.class), PATTERN_LOCK_SET);
                }
                break;
            case R.id.ll_clear_lock:
                if (patternSha1.equals("")) {
                    Snackbar.make(llClearLock, getString(R.string.toast_no_lock), Snackbar.LENGTH_SHORT).show();
                } else {
                    startActivityForResult(new Intent(getActivity(), PatternConfirmActivity.class), PATTERN_LOCK_CLEAR);
                }
                break;
            case R.id.ll_feedback:
//                FeedbackAPI.openFeedbackActivity(getActivity());    //百川反馈
                Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO);
                feedbackIntent.setData(Uri.parse("mailto:roocky08@gmail.com"));
                feedbackIntent.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.feedback_title) + " " + getString(R.string.app_version));
                startActivity(Intent.createChooser(feedbackIntent, getString(R.string.feedback_title)));
                break;
            case R.id.ll_about:
                Snackbar.make(llAbout, getString(R.string.toast_thanks), Snackbar.LENGTH_SHORT).show();
                //读取设备信息
                FileUtil.createTxt(DeviceJudge.getHandSetInfo(),
                        Environment.getExternalStorageDirectory() + getString(R.string.path_txt),
                        "设备信息" + ".txt");
                break;
            default:
                /**
                 * check()方法判断是否已经拥有该权限，若已拥有则直接进行备份&恢复操作，否则向用户发出请求
                 * 请求被处理后会回调onRequestPermissionsResult()方法
                 */
                idClick = v.getId();
                if (PermissionUtil.check(this, Manifest.permission.READ_EXTERNAL_STORAGE, PER_EXTERNAL_STORAGE)) {
                    backStore(idClick);   //备份 & 恢复
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PATTERN_LOCK_SET:
                if (resultCode == RESULT_OK) {
                    startActivity(new Intent(getActivity(), PatternSetActivity.class));
                }
                break;
            case PATTERN_LOCK_CLEAR:
                if (resultCode == RESULT_OK) {
                    editor.putString("patternSha1", "").apply();
                    Snackbar.make(llClearLock, getString(R.string.toast_clear_success), Snackbar.LENGTH_SHORT).show();
                }
                break;
            case SELECT_FILE:
                if (data != null) {
                    String result, src;
                    Uri uri = data.getData();
                    //根据Uri类型来获取备份文件的路径
                    if (uri.getPath().split(":").length == 1) {     //包含绝对路径的Uri（/storage/emulated/0/Mojian/MoJian2016-7-31_21-11.backup）
                        src = uri.getPath();
                    } else {                                        //包含相对路径的Uri（/document/primary:MoJian/MoJian2016-7-31_21-11.backup）
                        src = Environment.getExternalStorageDirectory() + "/" + uri.getPath().split(":")[1];
                    }
                    if (FileUtil.copy(src, getString(R.string.path_databases), "Mojian.db")) {
                        result = "恢复成功！";
                    } else {
                        result = "恢复失败！";
                    }
                    Snackbar.make(llRestore, result, Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //处理Android 6.0中permission请求完成事件
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PER_EXTERNAL_STORAGE:  //存储空间权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    backStore(idClick);     //若获取成功权限则进行备份&恢复操作
                } else {
                    Snackbar.make(llBackup, getString(R.string.toast_per_fail), Snackbar.LENGTH_SHORT).show();
                }
                return;
        }
    }

    //备份 & 恢复
    private void backStore(int id) {
        if (id == R.id.ll_backup) {
            adBackup = new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.set_backup))
                    .setMessage(getString(R.string.dialog_backup))
                    .setPositiveButton("确定", this)
                    .setNegativeButton("取消", null)
                    .show();
        } else if (id == R.id.ll_restore) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, SELECT_FILE);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (dialog == adBackup) {
            String result;
            if (FileUtil.copy(getString(R.string.path_databases) + "Mojian.db",
                    Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name_eng) + "/",
                    getString(R.string.app_name_eng)
                            + Mojian.year + "-" + (Mojian.month + 1) + "-" + Mojian.day + "_"     //年月日
                            + Mojian.hour + "-" + Mojian.minute + ".backup")) {                   //时分
                result = "备份成功！";
            } else {
                result = "备份失败！";
            }
            Snackbar.make(llBackup, result, Snackbar.LENGTH_SHORT).show();
        } else if (dialog == adSize) {
            editor.putInt("fontSize", which).apply();   //0->大字号，1->中字号，2->小字号
            dialog.dismiss();
        } else if (dialog == adDrawer) {
            editor.putInt("drawerPosition", which).apply();   //0->左，1->右
            dialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //不同Fragment菜单有区别
        getActivity().invalidateOptionsMenu();
        //刷新时间，保证备份文件名为最新时间
        Mojian.flushTime();
        tvBackupDetail.setText(getString(R.string.set_backup_detail, Mojian.year, Mojian.month + 1, Mojian.day,
                Mojian.hour, Mojian.minute));
        tvSizeDetail.setText(fontSize[preferences.getInt("fontSize", 1)]);
        tvDrawerDetail.setText(getString(R.string.set_drawer_detail, drawerPosition[preferences.getInt("drawerPosition", 0)]));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}