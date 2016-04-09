package net.roocky.moji.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.roocky.moji.Model.Diary;
import net.roocky.moji.Model.Note;
import net.roocky.moji.Moji;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roocky on 10/05.
 * 操作数据库
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    //日记表
    public static final String CREATE_DIARY = "create table diary ("
            + "id integer primary key autoincrement, "
            + "year integer, "
            + "month integer, "
            + "day integer, "
            + "content text)";
    //便笺表
    public static final String CREATE_NOTE = "create table note ("
            + "id integer primary key autoincrement, "
            + "year integer, "
            + "month integer, "
            + "day integer, "
            + "content text, "
            + "remind text)";

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

    public static List<? extends Diary> query(SQLiteDatabase database, String type, String[] columns, String selection, String[] selectionArgs) {
        List<Diary> diaryList = new ArrayList<>();
        List<Note> noteList = new ArrayList<>();
        Cursor cursor = database.query(type, columns, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int year = cursor.getInt(cursor.getColumnIndex("year"));
            int month = cursor.getInt(cursor.getColumnIndex("month"));
            int day = cursor.getInt(cursor.getColumnIndex("day"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            //根据type来决定存入哪个list中
            if (type.equals("diary")) {
                diaryList.add(new Diary(id, year, month, day, content));
            } else {
                String remind = cursor.getString(cursor.getColumnIndex("remind"));
                noteList.add(new Note(id, year, month, day, content, remind));
            }
        }
        cursor.close();
        return type.equals("diary") ? diaryList :noteList;
    }
}