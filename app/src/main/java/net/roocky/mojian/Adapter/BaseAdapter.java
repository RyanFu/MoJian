package net.roocky.mojian.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.roocky.mojian.Database.DatabaseHelper;
import net.roocky.mojian.Model.Base;
import net.roocky.mojian.Model.Diary;
import net.roocky.mojian.Model.Note;
import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;
import net.roocky.mojian.Util.ImageSpanUtil;
import net.roocky.mojian.Util.SDKVersion;

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
    public List<Diary> tempList = new ArrayList<>();

    private List<Integer> positionList = new ArrayList<>();

    protected int paper;

    /**
     * @param context
     * @param type              "diary" or "note"
     * @param columns           数据库query的列（需要把所有列写进去）
     * @param selection         查询条件
     * @param selectionArgs     查询参数
     * @param count             第几次获取数据(-1表示获取全部数据，主要用于日记fragment分次刷新数据)
     */
    public BaseAdapter(Context context, String type, String[] columns, String selection, String[] selectionArgs, int count) {
        database = new DatabaseHelper(context, "Mojian.db", null, 3).getWritableDatabase();
        listRefresh(type, Mojian.FLUSH_ALL, columns, selection, selectionArgs, count, -1);
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
        paper = baseList.get(baseList.size() - position - 1).getPaper();
        holder.tvDate.setBackgroundColor(Mojian.darkColors[paper]);
        if (positionList.contains(position)) {          //被选中，需要被删除的
            holder.tvContent.setBackgroundColor(Mojian.darkColors[paper]);
            if (type.equals("note")) {
                holder.tvRemind.setBackgroundColor(Mojian.darkColors[paper]);
            }
        } else {                                        //未被选中，不需要被删除的
            if (SDKVersion.isHigher(Build.VERSION_CODES.LOLLIPOP)) {
                holder.tvContent.setBackgroundResource(Mojian.ripples[paper]);
            } else {
                holder.tvContent.setBackgroundColor(Mojian.colors[paper]);
            }
            if (type.equals("note")){
                holder.tvRemind.setBackgroundColor(Mojian.colors[paper]);
            }
        }
        if (type.equals("diary")) {
            String strMonth = (Mojian.numbers[month].length() == 1 ? Mojian.numbers[month] : new StringBuilder(Mojian.numbers[month]).insert(1, "\n")).toString();
            String strDay = (Mojian.numbers[day - 1].length() == 1 ? Mojian.numbers[day - 1] : new StringBuilder(Mojian.numbers[day - 1]).insert(1, "\n")).toString();
            holder.tvDate.setText(strMonth + "\n · \n" + strDay);
        } else {
            holder.tvDate.setText(Mojian.numbers[month] + " · " + Mojian.numbers[day - 1]);
        }
        String strContent = ImageSpanUtil.getString(baseList.get(baseList.size() - position - 1).getContent()).toString();
        if (strContent.equals("")) {            //如果便笺内只有图片，则需在卡片上显示“图片”
            strContent = Mojian.context.getString(R.string.show_image_only);
        }
        holder.tvContent.setText(strContent);
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
        holder.cvItem.setTag(R.id.tag_content, baseList.get(baseList.size() - position - 1).getContent());
        holder.cvItem.setTag(R.id.tag_background, baseList.get(baseList.size() - position - 1).getBackground());
        holder.cvItem.setTag(R.id.tag_paper, baseList.get(baseList.size() - position - 1).getPaper());
        if (type.equals("diary")) {
            holder.cvItem.setTag(R.id.tag_weather, ((Diary) baseList.get(baseList.size() - position - 1)).getWeather());
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
                    case Mojian.FLUSH_ALL:
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
                    case Mojian.FLUSH_REMOVE:
                        diaryList.clear();
                        tempList.remove(tempList.size() - position - 1);
                        int start = tempList.size() - (count + 1) * 10 > 0 ? tempList.size() - (count + 1) * 10 : 0;
                        diaryList.addAll(tempList.subList(start, tempList.size()));
                        break;
                    case Mojian.FLUSH_ADD:
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
