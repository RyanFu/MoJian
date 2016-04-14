package net.roocky.mojian.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.roocky.mojian.R;

/**
 * Created by roocky on 03/17.
 * 日记RecyclerView适配器
 */
public class DiaryAdapter extends BaseAdapter {

    public DiaryAdapter(Context context, String[] columns, String selection, String[] selectionArgs, int count) {
        super(context, "diary", columns, selection, selectionArgs, count);
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
