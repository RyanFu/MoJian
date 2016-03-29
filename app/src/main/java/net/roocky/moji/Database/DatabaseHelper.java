package net.roocky.moji.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by roocky on 10/05.
 * 操作数据库
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    //日记表
    public static final String CREATE_DIARY = "create table diary ("
            + "id integer primary key autoincrement, "
            + "date text, "
            + "content text)";
    //便笺表
    public static final String CREATE_NOTE = "create table note ("
            + "id integer primary key autoincrement, "
            + "date text, "
            + "content text)";

    /**
     * 构造
     * @param context
     * @param name      数据库名称（文件名）
     * @param factory
     * @param version
     */
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DIARY);
        db.execSQL(CREATE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}