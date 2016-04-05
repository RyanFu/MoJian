package net.roocky.moji.Fragment;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.roocky.moji.Activity.ViewActivity;
import net.roocky.moji.Adapter.BaseAdapter;
import net.roocky.moji.Adapter.NoteAdapter;
import net.roocky.moji.R;

/**
 * Created by roocky on 04/05.
 * DiaryFragment和NoteFragment的基类
 */
public class BaseFragment extends Fragment implements NoteAdapter.OnItemClickListener {

    //CardView点击事件
    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), ViewActivity.class);
        intent.putExtra("id", (view.findViewById(R.id.cv_item)).getTag().toString());     //id作为删除和修改的标识
        intent.putExtra("content", ((TextView) (view.findViewById(R.id.tv_content))).getText().toString());
        if (view.findViewById(R.id.tv_remind) != null) {    //判断当前的Fragment是diary还是note
            intent.putExtra("from", "note");
            intent.putExtra("remind", ((TextView) (view.findViewById(R.id.tv_remind))).getText().toString());
        } else {
            intent.putExtra("from", "diary");
        }
        startActivity(intent);
    }

    //根据滚动方向决定FAB是否显示
    protected void addOnScrollListener(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    }

    //RecyclerView刷新
    protected <T extends BaseAdapter> void flush(T adapter, String type) {
        adapter.listRefresh(type);
        adapter.notifyDataSetChanged();
    }
}
