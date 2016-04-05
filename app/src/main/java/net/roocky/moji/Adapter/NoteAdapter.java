package net.roocky.moji.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.roocky.moji.R;

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

    /**
     * 如果不设置else的话，会自动将上一条的tvRemind拿来显示，原因不清楚。。。
     *
     * tvRemind默认是不显示的，只有下面的判断中不是null时才会显示
     * 但是实际情况是如果没有设置remind的话，调试可以看到list里面的remind就是null，但是会显示以前的remind
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        //数据在数据库中是倒序排列的，即日期最早的数据在最前面，所以此处的index需要减去position
        if (noteList.get(noteList.size() - position - 1).getRemind() != null) {
            holder.tvRemind.setVisibility(View.VISIBLE);
            holder.tvRemind.setText(noteList.get(noteList.size() - position - 1).getRemind());
        } else {
            holder.tvRemind.setText("");
            holder.tvRemind.setVisibility(View.GONE);
        }
    }
}