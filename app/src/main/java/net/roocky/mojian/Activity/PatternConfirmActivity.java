package net.roocky.mojian.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import net.roocky.mojian.R;

import java.util.List;

import me.zhanghai.android.patternlock.ConfirmPatternActivity;
import me.zhanghai.android.patternlock.PatternUtils;
import me.zhanghai.android.patternlock.PatternView;

/**
 * Created by roocky on 04/24.
 * 验证图案Activity
 */
public class PatternConfirmActivity extends ConfirmPatternActivity {
    @Override
    protected boolean isStealthModeEnabled() {
        // TODO: Return the value from SharedPreferences.
        return false;
    }

    @Override
    protected boolean isPatternCorrect(List<PatternView.Cell> pattern) {
        // TODO: Get saved pattern sha1.
        SharedPreferences preferences = getSharedPreferences("mojian", MODE_PRIVATE);
        String patternSha1 = preferences.getString("patternSha1", "");
        return TextUtils.equals(PatternUtils.patternToSha1String(pattern), patternSha1);
    }

    @Override
    protected void onForgotPassword() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_forget_pattern))
                .setMessage(getString(R.string.dialog_forget_resolve))
                .setPositiveButton("确定", null)
                .show();
    }
}
