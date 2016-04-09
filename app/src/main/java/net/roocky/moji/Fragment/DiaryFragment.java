package net.roocky.moji.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.roocky.moji.Activity.ViewActivity;
import net.roocky.moji.Adapter.DiaryAdapter;
import net.roocky.moji.Moji;
import net.roocky.moji.R;
import net.roocky.moji.Widget.BottomRecyclerView;

/**
 * Created by roocky on 03/16.
 * 日記Fragment
 */
public class DiaryFragment extends BaseFragment implements BottomRecyclerView.OnBottomListener {
    private DiaryAdapter adapter;
    private int count = 0;      //第几次刷新RecyclerView（日记的RecyclerView需要分次刷新，每次10条数据）
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        BottomRecyclerView rvDiary = (BottomRecyclerView)view.findViewById(R.id.rv_diary);
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new DiaryAdapter(getContext(), null, null, null, 0);

        rvDiary.setLayoutManager(manager);
        rvDiary.setAdapter(adapter);
        //当由当前fragment切换至其他fragment时需要清空删除list
        deleteList.clear();
        positionList.clear();
        //根据滚动方向决定FAB是否显示
        super.addOnScrollListener(rvDiary);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        rvDiary.setOnBottomListener(this);
        return view;
    }

    @Override
    public void OnBottom() {
        flush(Moji.FLUSH_ALL, 0, ++count);
    }

    public void flush(int action, int position, int count) {
        super.flush(adapter, "diary", action, position, count);
    }

    @Override
    public void onResume() {
        super.onResume();
        count = 0;
    }
}