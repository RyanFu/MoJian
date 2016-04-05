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
import net.roocky.moji.Model.Note;
import net.roocky.moji.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roockty on 04/05.
 * DiaryAdapter和NoteAdapter的基类
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.ViewHolder>  {
    protected DatabaseHelper databaseHelper;
    protected List<Diary> diaryList = new ArrayList<>();
    protected List<Note> noteList = new ArrayList<>();
    protected OnItemClickListener onItemClickListener;

    private String type;

    public BaseAdapter(Context context, String type) {
        databaseHelper = new DatabaseHelper(context, "Moji.db", null, 1);
        listRefresh(type);
        this.type = type;
    }

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        List<? extends Diary> baseList;
        if (type.equals("diary")) {
            baseList = diaryList;
        } else {
            baseList = noteList;
        }
        //数据在数据库中是倒序排列的，即日期最早的数据在最前面，所以此处的index需要减去position
        holder.tvDate.setText(baseList.get(baseList.size() - position - 1).getDate());
        holder.tvContent.setText(baseList.get(baseList.size() - position - 1).getContent());
        holder.cvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, holder.getLayoutPosition());
            }
        });
        //将该item在数据库中的id存入CardView中
        holder.cvItem.setTag(baseList.get(baseList.size() - position - 1).getId());
    }

    @Override
    public int getItemCount() {
        return type.equals("diary") ? diaryList.size() : noteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvDate;
        protected TextView tvContent;
        protected TextView tvRemind;
        protected CardView cvItem;
        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView)itemView.findViewById(R.id.tv_date);
            tvContent = (TextView)itemView.findViewById(R.id.tv_content);
            if (type.equals("note")) {
                tvRemind = (TextView) itemView.findViewById(R.id.tv_remind);
            }
            cvItem = (CardView)itemView.findViewById(R.id.cv_item);
        }
    }

    //刷新listDate&listContent
    public void listRefresh(String type) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Cursor cursor = database.query(type, null, null, null, null, null, null);
        diaryList.clear();
        noteList.clear();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                //根据type来决定存入哪个list中
                if (type.equals("diary")) {
                    diaryList.add(new Diary(id, date, content));
                } else {
                    String remind = cursor.getString(cursor.getColumnIndex("remind"));
                    noteList.add(new Note(id, date, content, remind));
                }
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
