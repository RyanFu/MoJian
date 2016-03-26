package net.roocky.moji.Fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.roocky.moji.Adapter.DiaryAdapter;
import net.roocky.moji.R;

/**
 * Created by roocky on 03/16.
 */
public class DiaryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);
        RecyclerView rvDiary = (RecyclerView)view.findViewById(R.id.rv_diary);
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        DiaryAdapter adapter = new DiaryAdapter();
        rvDiary.setLayoutManager(manager);
        rvDiary.setAdapter(adapter);
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
        return view;
    }
}
