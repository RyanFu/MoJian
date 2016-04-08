package net.roocky.moji.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.roocky.moji.Database.DatabaseHelper;
import net.roocky.moji.Model.Diary;
import net.roocky.moji.Model.Note;
import net.roocky.moji.Moji;
import net.roocky.moji.R;
import net.roocky.moji.Util.SDKVersion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roockty on 04/05.
 * DiaryAdapter和NoteAdapter的基类
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {
    protected SQLiteDatabase database;
    protected List<Diary> diaryList = new ArrayList<>();
    protected List<Note> noteList = new ArrayList<>();
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;

    private String type;

    /**
     * @param context
     * @param type              "diary" or "note"
     * @param columns           数据库query的列（需要把所有列写进去）
     * @param selection         查询条件
     * @param selectionArgs     查询参数
     */
    public BaseAdapter(Context context, String type, String[] columns, String selection, String[] selectionArgs) {
        database = new DatabaseHelper(context, "Moji.db", null, 1).getWritableDatabase();
        listRefresh(type, columns, selection, selectionArgs);
        this.type = type;
    }

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        List<? extends Diary> baseList = type.equals("diary") ? diaryList : noteList;
        //数据在数据库中是倒序排列的，即日期最早的数据在最前面，所以此处的index需要减去position
        holder.tvDate.setText(baseList.get(baseList.size() - position - 1).getDate());
        holder.tvContent.setText(baseList.get(baseList.size() - position - 1).getContent());
        holder.cvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, holder.getLayoutPosition());
            }
        });
        holder.cvItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClickListener.onItemLongClick(v, holder.getLayoutPosition());
                return true;
            }
        });
        //将该item在数据库中的id存入CardView中
        holder.cvItem.setTag(baseList.get(baseList.size() - position - 1).getId());
        //设置item的点击效果（5.0以上版本）
        if (SDKVersion.judge(Build.VERSION_CODES.LOLLIPOP)) {
            holder.tvContent.findViewById(R.id.tv_content).setBackgroundResource(R.drawable.bg_ripple_white);
        } else {
            holder.tvContent.findViewById(R.id.tv_content).setBackgroundColor(Color.WHITE);
        }
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
            if (type.equals("note")) {      //只有当前处于NoteFragment时才可以得到tvRemind
                tvRemind = (TextView) itemView.findViewById(R.id.tv_remind);
            }
            cvItem = (CardView)itemView.findViewById(R.id.cv_item);
        }
    }

    //刷新listDate&listContent
    public void listRefresh(String type, String[] columns, String selection, String[] selectionArgs) {
        Cursor cursor = database.query(type, columns, selection, selectionArgs, null, null, null);
        diaryList.clear();
        noteList.clear();
        while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int month = cursor.getInt(cursor.getColumnIndex("month"));
                int day = cursor.getInt(cursor.getColumnIndex("day"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                //根据type来决定存入哪个list中
                if (type.equals("diary")) {
                    //如果“月”或“日”的长度不为“1”，需要添加“/n”
                    String strMonth = (Moji.numbers[month].length() == 1 ? Moji.numbers[month] : new StringBuilder(Moji.numbers[month]).insert(1, "\n")).toString();
                    String strDay = (Moji.numbers[day - 1].length() == 1 ? Moji.numbers[day - 1] : new StringBuilder(Moji.numbers[day - 1]).insert(1, "\n")).toString();
                    diaryList.add(new Diary(id, strMonth + "\n · \n" + strDay, content));
                } else {
                    String remind = cursor.getString(cursor.getColumnIndex("remind"));
                    noteList.add(new Note(id, Moji.numbers[month] + " · " + Moji.numbers[day - 1], content, remind));
                }
        }
        cursor.close();
    }

    //获取哪些天记了日记
    public List<CalendarDay> getDayList() {
        List<CalendarDay> dayList = new ArrayList<>();
        dayList.clear();
        Cursor cursor = database.query(type, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
                int year = cursor.getInt(cursor.getColumnIndex("year"));
                int month = cursor.getInt(cursor.getColumnIndex("month"));
                int day = cursor.getInt(cursor.getColumnIndex("day"));
                dayList.add(CalendarDay.from(year, month, day));
        }
        cursor.close();
        return dayList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
}
