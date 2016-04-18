package net.roocky.mojian.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.umeng.update.UmengUpdateAgent;

import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;
import net.roocky.mojian.Util.FileCopy;
import net.roocky.mojian.Util.PermissionUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by roocky on 03/16.
 * 设置Fragment
 */
public class SettingFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.ll_backup)
    LinearLayout llBackup;
    @Bind(R.id.ll_restore)
    LinearLayout llRestore;
    @Bind(R.id.ll_update)
    LinearLayout llUpdate;
    @Bind(R.id.ll_feedback)
    LinearLayout llFeedback;
    @Bind(R.id.ll_about)
    LinearLayout llAbout;

    private final int PER_EXTERNAL_STORAGE = 0;
    private int idClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);

        setOnClickListener();

        return view;
    }

    private void setOnClickListener() {
        llBackup.setOnClickListener(this);
        llRestore.setOnClickListener(this);
        llUpdate.setOnClickListener(this);
        llFeedback.setOnClickListener(this);
        llAbout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_update:
                Toast.makeText(getContext(), getString(R.string.toast_update), Toast.LENGTH_SHORT).show();
                Mojian.isAutoUpdate = false;        //标识当前更新为手动更新
                UmengUpdateAgent.forceUpdate(getActivity());
                break;
            case R.id.ll_feedback:
                FeedbackAPI.openFeedbackActivity(getActivity());    //百川反馈
                break;
            case R.id.ll_about:
                Snackbar.make(llAbout, getString(R.string.toast_thanks), Snackbar.LENGTH_SHORT).show();
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
        String result;
        if (id == R.id.ll_backup) {
            if (FileCopy.copy(getString(R.string.path_databases) + "Mojian.db", getString(R.string.path_sdcard))) {
                result = "备份成功！";
            } else {
                result = "备份失败！";
            }
            Snackbar.make(llBackup, result, Snackbar.LENGTH_SHORT).show();
        } else {
            if (FileCopy.copy(getString(R.string.path_sdcard), getString(R.string.path_databases) + "Mojian.db")) {
                result = "恢复成功！";
            } else {
                result = "恢复失败！";
            }
            Snackbar.make(llRestore, result, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}