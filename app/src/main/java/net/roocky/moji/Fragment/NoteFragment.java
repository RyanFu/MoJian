package net.roocky.moji.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.roocky.moji.Activity.ViewActivity;
import net.roocky.moji.Adapter.NoteAdapter;
import net.roocky.moji.R;

/**
 * Created by roocky on 03/16.
 * 便箋Fragment
 */
public class NoteFragment extends Fragment implements NoteAdapter.OnItemClickListener {
    private NoteAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        RecyclerView rvNote = (RecyclerView)view.findViewById(R.id.rv_note);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        adapter = new NoteAdapter(getContext());

        rvNote.setLayoutManager(manager);
        rvNote.setAdapter(adapter);
        //根据滚动方向决定FAB是否显示
        rvNote.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    ((FloatingActionButton) getActivity().findViewById(R.id.fab_add)).hide();
                } else {
                    ((FloatingActionButton) getActivity().findViewById(R.id.fab_add)).show();
                }
            }
        });
        adapter.setOnItemClickListener(this);
        return view;
    }

    //CardView点击事件
    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), ViewActivity.class);
        intent.putExtra("from", "note");
        intent.putExtra("id", (view.findViewById(R.id.cv_note_item)).getTag().toString());     //id作为删除和修改的标识
        intent.putExtra("content", ((TextView) (view.findViewById(R.id.tv_content))).getText().toString());
        //如果提醒时间未设置的话，此处取得的文本为""
        intent.putExtra("remind", ((TextView) (view.findViewById(R.id.tv_remind))).getText().toString());
        startActivity(intent);
    }

    public void flush() {   //notify函数的参数position是从1开始的
//        adapter.notifyItemInserted(1);     //在position为1的位置插入
        adapter.listRefresh();
//        adapter.notifyItemRangeChanged(0, adapter.getItemCount());    //刷新时从position为0的位置以后刷新
        adapter.notifyDataSetChanged();
    }
}