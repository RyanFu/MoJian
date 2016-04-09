package net.roocky.moji.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.roocky.moji.Activity.ViewActivity;
import net.roocky.moji.Adapter.BaseAdapter;
import net.roocky.moji.Adapter.NoteAdapter;
import net.roocky.moji.Model.Diary;
import net.roocky.moji.Model.Note;
import net.roocky.moji.Moji;
import net.roocky.moji.R;
import net.roocky.moji.Util.SDKVersion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roocky on 04/05.
 * DiaryFragment和NoteFragment的基类
 */
public class BaseFragment extends Fragment implements BaseAdapter.OnItemClickListener,
        BaseAdapter.OnItemLongClickListener {
    public boolean isDeleting = false;
    private String idSelect;

    public List<String> deleteList = new ArrayList<>();
    public List<Integer> positionList = new ArrayList<>();
    public List<Diary> diaryList = new ArrayList<>();
    public List<Note> noteList = new ArrayList<>();

    //CardView点击事件
    @Override
    public void onItemClick(View view, int position) {
        idSelect = (view.findViewById(R.id.cv_item)).getTag(R.id.tag_id).toString();
        if (!isDeleting) {      //若未处于删除状态，直接进行跳转
            Intent intent = new Intent(getActivity(), ViewActivity.class);
            intent.putExtra("id", idSelect);     //id作为删除和修改的标识
            intent.putExtra("content", ((TextView) (view.findViewById(R.id.tv_content))).getText().toString());
            if (view.findViewById(R.id.tv_remind) != null) {    //判断当前的Fragment是diary还是note
                intent.putExtra("from", "note");
                intent.putExtra("remind", ((TextView) (view.findViewById(R.id.tv_remind))).getText().toString());
            } else {
                intent.putExtra("from", "diary");
            }
            startActivity(intent);
        } else {        //若处于删除状态，选择被点击的该项
            if (deleteList.contains(idSelect)) {    //如果已选则设置为未选
                if (SDKVersion.judge(Build.VERSION_CODES.LOLLIPOP)) {
                    view.findViewById(R.id.tv_content).setBackgroundResource(R.drawable.bg_ripple_white);
                } else {
                    view.findViewById(R.id.tv_content).setBackgroundColor(Color.WHITE);
                }
                deleteList.remove(idSelect);
                positionList.remove(Integer.valueOf(position));
                if (deleteList.size() == 0) {
                    isDeleting = false;     //当最后一个被选中的项被取消时需退出删除状态
                    ((FloatingActionButton) getActivity().findViewById(R.id.fab_add)).setImageResource(R.mipmap.ic_add_white_24dp);
                }
                if (view.findViewById(R.id.tv_remind) != null) {
                    noteList.remove(new Note(Integer.parseInt(idSelect),
                            (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_year),
                            (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_month),
                            (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_day),
                            ((TextView)view.findViewById(R.id.tv_content)).getText().toString(),
                            ((TextView)view.findViewById(R.id.tv_remind)).getText().toString()));
                } else {
                    diaryList.remove(new Diary(Integer.parseInt(idSelect),
                            (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_year),
                            (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_month),
                            (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_day),
                            ((TextView)view.findViewById(R.id.tv_content)).getText().toString()));
                }

            } else {        //如果未选则设置为已选
                view.findViewById(R.id.tv_content).setBackgroundColor(0xff9e9e9e);    //grey_500
                deleteList.add(idSelect);
                positionList.add(position);
                if (view.findViewById(R.id.tv_remind) != null) {
                    noteList.add(new Note(Integer.parseInt(idSelect),
                            (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_year),
                            (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_month),
                            (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_day),
                            ((TextView)view.findViewById(R.id.tv_content)).getText().toString(),
                            ((TextView)view.findViewById(R.id.tv_remind)).getText().toString()));
                } else {
                    diaryList.add(new Diary(Integer.parseInt(idSelect),
                            (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_year),
                            (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_month),
                            (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_day),
                            ((TextView)view.findViewById(R.id.tv_content)).getText().toString()));
                }
            }
        }
    }

    //长按删除事件
    @Override
    public void onItemLongClick(View view, int position) {
        idSelect = (view.findViewById(R.id.cv_item)).getTag(R.id.tag_id).toString();
        isDeleting = true;
        deleteList.clear();
        positionList.clear();
        diaryList.clear();
        noteList.clear();
        view.findViewById(R.id.tv_content).setBackgroundColor(0xff9e9e9e);    //grey_500
        ((FloatingActionButton) getActivity().findViewById(R.id.fab_add)).setImageResource(R.mipmap.ic_delete_white_24dp);
        deleteList.add((view.findViewById(R.id.cv_item)).getTag(R.id.tag_id).toString());
        positionList.add(position);
        if (view.findViewById(R.id.tv_remind) != null) {
            noteList.add(new Note(Integer.parseInt(idSelect),
                    (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_year),
                    (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_month),
                    (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_day),
                    ((TextView)view.findViewById(R.id.tv_content)).getText().toString(),
                    ((TextView)view.findViewById(R.id.tv_remind)).getText().toString()));
        } else {
            diaryList.add(new Diary(Integer.parseInt(idSelect),
                    (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_year),
                    (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_month),
                    (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_day),
                    ((TextView)view.findViewById(R.id.tv_content)).getText().toString()));
        }
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

    /**
     * RecyclerView刷新
     * @param adapter   RecyclerView的适配器
     * @param type      “diary”或者“note”
     * @param action    具体的刷新行为
     * @param position  位置（插入的位置）
     * @param <T>
     */
    protected <T extends BaseAdapter> void flush(T adapter, String type, int action, int position) {
        adapter.listRefresh(type, null, null, null);
        switch (action) {
            case 0:         //插入刷新
                adapter.notifyItemInserted(position);
                break;
            case 1:         //移除刷新
                adapter.notifyItemRemoved(position);
                break;
            case 2:         //全部刷新
                adapter.notifyDataSetChanged();
                break;
        }
    }
}
