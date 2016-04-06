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
public class NoteFragment extends BaseFragment {
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
        super.addOnScrollListener(rvNote);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        return view;
    }

    public void flush(int action, int position) {
        super.flush(adapter, "note", action, position);
    }
}