package net.roocky.mojian.Activity;

import android.content.SharedPreferences;

import java.util.List;

import me.zhanghai.android.patternlock.PatternUtils;
import me.zhanghai.android.patternlock.PatternView;
import me.zhanghai.android.patternlock.SetPatternActivity;

/**
 * Created by roocky on 04/24.
 * 设置图案解锁Activity
 */
public class PatternSetActivity extends SetPatternActivity {
    @Override
    protected void onSetPattern(List<PatternView.Cell> pattern) {
        String patternSha1 = PatternUtils.patternToSha1String(pattern);
        SharedPreferences.Editor editor = getSharedPreferences("mojian", MODE_PRIVATE).edit();
        editor.putString("patternSha1", patternSha1).apply();
    }
}
