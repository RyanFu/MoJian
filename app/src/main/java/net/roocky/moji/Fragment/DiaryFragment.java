package net.roocky.moji.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.roocky.moji.Activity.ViewActivity;
import net.roocky.moji.Adapter.DiaryAdapter;
import net.roocky.moji.R;

/**
 * Created by roocky on 03/16.
 * 日記Fragment
 */
public class DiaryFragment extends Fragment implements DiaryAdapter.OnItemClickListener {
    private DiaryAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        RecyclerView rvDiary = (RecyclerView)view.findViewById(R.id.rv_diary);
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new DiaryAdapter(getContext());

        rvDiary.setLayoutManager(manager);
        rvDiary.setAdapter(adapter);
        //根据滚动方向决定FAB是否显示
        rvDiary.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    ((FloatingActionButton)getActivity().findViewById(R.id.fab_add)).hide();
                } else {
                    ((FloatingActionButton)getActivity().findViewById(R.id.fab_add)).show();
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
        intent.putExtra("from", "diary");
        intent.putExtra("id", (view.findViewById(R.id.cv_diary_item)).getTag().toString());     //id作为删除和修改的标识
        intent.putExtra("content", ((TextView) (view.findViewById(R.id.tv_content))).getText().toString());
        startActivity(intent);
    }

    public void flush() {   //notify函数的参数position是从1开始的
//        adapter.notifyItemInserted(1);     //在position为1的位置插入
        adapter.listRefresh();
//        adapter.notifyItemRangeChanged(0, adapter.getItemCount());    //刷新时从position为0的位置以后刷新
        adapter.notifyDataSetChanged();
    }
}