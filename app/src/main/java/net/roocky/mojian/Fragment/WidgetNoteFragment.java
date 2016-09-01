package net.roocky.mojian.Fragment;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import net.roocky.mojian.APPWidgetConfigure.ItemAppWidgetConfigure;
import net.roocky.mojian.Activity.ViewActivity;
import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;

/**
 * Created by Roocky on 2016/8/20 0020.
 */
public class WidgetNoteFragment extends NoteFragment {

    @Override
    public void onItemClick(View view, int position) {
        Activity activity = getActivity();
        //设置小部件外观（纸张颜色、背景图片）
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
        RemoteViews remoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.appwidget_item);
        remoteViews.setInt(R.id.ll_content, "setBackgroundColor", Mojian.colors[(Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_paper)]);
        remoteViews.setImageViewResource(R.id.iv_bottom, Mojian.backgrounds[(Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_background)]);
        remoteViews.setTextViewText(R.id.tv_content, (String)view.findViewById(R.id.cv_item).getTag(R.id.tag_content));
        //绑定点击跳转事件
        Intent intent = new Intent(getContext(), ViewActivity.class);
        intent.putExtra("from", "note");
        intent.putExtra("id", (view.findViewById(R.id.cv_item)).getTag(R.id.tag_id).toString());
        intent.putExtra("content", (String)view.findViewById(R.id.cv_item).getTag(R.id.tag_content));
        intent.putExtra("background", (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_background));
        intent.putExtra("paper", (Integer)view.findViewById(R.id.cv_item).getTag(R.id.tag_paper));
        intent.putExtra("remind", ((TextView) (view.findViewById(R.id.tv_remind))).getText().toString());
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), position, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.ll_content, pendingIntent);
        appWidgetManager.updateAppWidget(ItemAppWidgetConfigure.getAppWidgetId(), remoteViews);
        //返回结果（目前没有用到返回的结果）
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ItemAppWidgetConfigure.getAppWidgetId());
        activity.setResult(Activity.RESULT_OK, resultValue);
        activity.finish();
    }
}
