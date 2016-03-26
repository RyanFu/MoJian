package net.roocky.moji.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.roocky.moji.Adapter.NoteAdapter;
import net.roocky.moji.R;

/**
 * Created by roocky on 03/16.
 *
 */
public class NoteFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        RecyclerView rvNote = (RecyclerView)view.findViewById(R.id.rv_note);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        NoteAdapter adapter = new NoteAdapter();
        rvNote.setLayoutManager(manager);
        rvNote.setAdapter(adapter);
        return view;
    }
}