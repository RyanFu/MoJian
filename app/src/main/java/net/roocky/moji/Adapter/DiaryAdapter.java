package net.roocky.moji.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.roocky.moji.Database.DatabaseHelper;
import net.roocky.moji.Model.Diary;
import net.roocky.moji.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roocky on 03/17.
 * 日记RecyclerView适配器
 */
public class DiaryAdapter extends BaseAdapter {

    public DiaryAdapter(Context context, String[] columns, String selection, String[] selectionArgs) {
        super(context, "diary", columns, selection, selectionArgs);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }
}
