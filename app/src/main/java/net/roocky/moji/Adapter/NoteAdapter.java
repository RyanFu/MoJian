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
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private DatabaseHelper databaseHelper;
    private List<Note> noteList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public NoteAdapter(Context context) {
        databaseHelper = new DatabaseHelper(context, "Moji.db", null, 1);
        listRefresh();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //数据在数据库中是倒序排列的，即日期最早的数据在最前面，所以此处的index需要减去position
        holder.tvDate.setText(noteList.get(noteList.size() - position - 1).getDate());
        holder.tvContent.setText(noteList.get(noteList.size() - position - 1).getContent());
        if (noteList.get(noteList.size() - position - 1).getRemind() != null) {
            holder.tvRemind.setVisibility(View.VISIBLE);
            holder.tvRemind.setText(noteList.get(noteList.size() - position - 1).getRemind());
        }
        holder.cvNoteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, holder.getLayoutPosition());
            }
        });
        //将该item在数据库中的id存入CardView中
        holder.cvNoteItem.setTag(noteList.get(noteList.size() - position - 1).getId());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvContent;
        private TextView tvRemind;
        private CardView cvNoteItem;
        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView)itemView.findViewById(R.id.tv_date);
            tvContent = (TextView)itemView.findViewById(R.id.tv_content);
            tvRemind = (TextView)itemView.findViewById(R.id.tv_remind);
            cvNoteItem = (CardView)itemView.findViewById(R.id.cv_note_item);
        }
    }

    //刷新listDate&listContent
    public void listRefresh() {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Cursor cursor = database.query("note", null, null, null, null, null, null);
        noteList.clear();
        if (cursor.moveToFirst()) {
            do {
                noteList.add(new Note(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("date")),
                        cursor.getString(cursor.getColumnIndex("content")),
                        cursor.getString(cursor.getColumnIndex("remind"))));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
