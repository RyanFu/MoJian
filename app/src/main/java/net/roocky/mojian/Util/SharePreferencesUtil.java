package net.roocky.mojian.Util;

import android.content.Context;
import android.content.SharedPreferences;

import net.roocky.mojian.Const;

import java.util.Map;

/**
 * Created by roocky on 09/05.
 */
public class SharePreferencesUtil {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    private SharePreferencesUtil(Context context) {
        preferences = context.getSharedPreferences(Const.mainShareP, context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    private SharePreferencesUtil(Context context, String name) {
        preferences = context.getSharedPreferences(name, context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static SharePreferencesUtil getInstance(Context context) {
        return new SharePreferencesUtil(context);
    }

    public static SharePreferencesUtil getInstance(Context context, String name) {
        return new SharePreferencesUtil(context, name);
    }

    /*--------------------------------------Put-----------------------------------------------*/
    public void putInt(String key, int value) {
        editor.putInt(key, value).apply();
    }

    public void putString(String key, String value) {
        editor.putString(key, value).apply();;
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }

    /*--------------------------------------Get-----------------------------------------------*/
    public Map<String, ?> getAll() {
        return preferences.getAll();
    }

    public int getInt(String key, int defValue) {
        return preferences.getInt(key, defValue);
    }

    public String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    public boolean getString(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    /*--------------------------------------Other-----------------------------------------------*/
    public void remove(String key) {
        editor.remove(key).apply();
    }
}
