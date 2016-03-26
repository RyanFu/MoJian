package net.roocky.moji.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.roocky.moji.R;

/**
 * Created by roocky on 03/17.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    public NoteAdapter() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvDate.setText("");
        holder.tvContent.setText("");
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvContent;
        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView)itemView.findViewById(R.id.tv_date);
            tvContent = (TextView)itemView.findViewById(R.id.tv_content);
        }
    }
}
