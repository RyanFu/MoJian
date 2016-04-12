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
import net.roocky.moji.Model.Base;
import net.roocky.moji.Model.Diary;
import net.roocky.moji.Model.Note;
import net.roocky.moji.Moji;
import net.roocky.moji.R;
import net.roocky.moji.Util.SDKVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    public List<Diary> tempList = new ArrayList<>();

    private List<Integer> positionList = new ArrayList<>();

    /**
     * @param context
     * @param type              "diary" or "note"
     * @param columns           数据库query的列（需要把所有列写进去）
     * @param selection         查询条件
     * @param selectionArgs     查询参数
     * @param count             第几次获取数据(-1表示获取全部数据，主页=要用于日记fragment分次刷新数据)
     */
    public BaseAdapter(Context context, String type, String[] columns, String selection, String[] selectionArgs, int count) {
        database = new DatabaseHelper(context, "Moji.db", null, 1).getWritableDatabase();
        listRefresh(type, Moji.FLUSH_ALL, columns, selection, selectionArgs, count, -1);
        this.type = type;
    }

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        List<? extends Base> baseList = type.equals("diary") ? diaryList : noteList;
        //数据在数据库中是倒序排列的，即日期最早的数据在最前面，所以此处的index需要减去position
        int month = baseList.get(baseList.size() - position - 1).getMonth();
        int day = baseList.get(baseList.size() - position - 1).getDay();
        //设置显示
        if (type.equals("diary")) {
            String strMonth = (Moji.numbers[month].length() == 1 ? Moji.numbers[month] : new StringBuilder(Moji.numbers[month]).insert(1, "\n")).toString();
            String strDay = (Moji.numbers[day - 1].length() == 1 ? Moji.numbers[day - 1] : new StringBuilder(Moji.numbers[day - 1]).insert(1, "\n")).toString();
            holder.tvDate.setText(strMonth + "\n · \n" + strDay);
        } else {
            holder.tvDate.setText(Moji.numbers[month] + " · " + Moji.numbers[day - 1]);
        }
        holder.tvContent.setText(baseList.get(baseList.size() - position - 1).getContent());
        //绑定监听事件
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
        //将该item在数据库中的数据存入CardView中
        holder.cvItem.setTag(R.id.tag_id, baseList.get(baseList.size() - position - 1).getId());
        holder.cvItem.setTag(R.id.tag_year, baseList.get(baseList.size() - position - 1).getYear());
        holder.cvItem.setTag(R.id.tag_month, baseList.get(baseList.size() - position - 1).getMonth());
        holder.cvItem.setTag(R.id.tag_day, baseList.get(baseList.size() - position - 1).getDay());
        if (type.equals("diary")) {
            holder.cvItem.setTag(R.id.tag_weather, ((Diary) baseList.get(baseList.size() - position - 1)).getWeather());
        }
        //设置item的点击效果（5.0以上版本）
        if (positionList.contains(position)) {
            holder.tvContent.findViewById(R.id.tv_content).setBackgroundColor(0xff9e9e9e);
        } else {
            if (SDKVersion.judge(Build.VERSION_CODES.LOLLIPOP)) {
                holder.tvContent.findViewById(R.id.tv_content).setBackgroundResource(R.drawable.bg_ripple_white);
            } else {
                holder.tvContent.findViewById(R.id.tv_content).setBackgroundColor(Color.WHITE);
            }
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

    //设置被选中的item的positionList（被选中的item滑出屏幕后再次滑入屏幕保证依然是灰色背景）
    public void setPositionList(List<Integer> positionList) {
        this.positionList = positionList;
    }

    //刷新listDate&listContent
    public void listRefresh(String type, int action, String[] columns, String selection, String[] selectionArgs, int count, int position) {
        if (type.equals("diary")) {
            if (count == -1) {
                diaryList.clear();
                diaryList = (List<Diary>)DatabaseHelper.query(database, "diary", columns, selection, selectionArgs);
            } else {
                switch (action) {   //根据刷新的行为来决定更新diaryList的具体方式
                    case Moji.FLUSH_ALL:
                        if (count == 0) {       //第一次需要查询数据库
                            diaryList.clear();
                            tempList = (List<Diary>) DatabaseHelper.query(database, "diary", columns, selection, selectionArgs);
                        }
                        if (tempList.size() - count * 10 > 0) {     //判断是否还有新数据可以添加到diaryList中
                            //如果此次添加的数据不足10个，则把start设置为0
                            int start = tempList.size() - (count + 1) * 10 > 0 ? tempList.size() - (count + 1) * 10 : 0;
                            int end = tempList.size() - count * 10;
                            diaryList.addAll(0, tempList.subList(start, end));
                        }
                        break;
                    case Moji.FLUSH_REMOVE:
                        diaryList.clear();
                        tempList.remove(tempList.size() - position - 1);
                        int start = tempList.size() - (count + 1) * 10 > 0 ? tempList.size() - (count + 1) * 10 : 0;
                        diaryList.addAll(tempList.subList(start, tempList.size()));
                        break;
                    case Moji.FLUSH_ADD:
                        //listRefresh方法并没有将恢复的日记对象传进来，所以此处每恢复一条查询一次
                        tempList = (List<Diary>) DatabaseHelper.query(database, "diary", columns, selection, selectionArgs);
                        diaryList.clear();
                        int begin = tempList.size() - (count + 1) * 10 > 0 ? tempList.size() - (count + 1) * 10 : 0;
                        diaryList.addAll(tempList.subList(begin, tempList.size()));
                        break;
                }
            }
        } else {
            noteList.clear();
            noteList = (List<Note>)DatabaseHelper.query(database, "note", columns, selection, selectionArgs);
        }
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
