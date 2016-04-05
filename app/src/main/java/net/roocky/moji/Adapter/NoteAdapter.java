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
import net.roocky.moji.Model.Note;
import net.roocky.moji.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roocky on 03/17.
 * 便笺RecyclerView适配器
 */
public class NoteAdapter extends BaseAdapter {

    public NoteAdapter(Context context) {
        super(context, "note");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        //数据在数据库中是倒序排列的，即日期最早的数据在最前面，所以此处的index需要减去position
        if (noteList.get(noteList.size() - position - 1).getRemind() != null) {
            holder.tvRemind.setVisibility(View.VISIBLE);
            holder.tvRemind.setText(noteList.get(noteList.size() - position - 1).getRemind());
        }
    }
}
