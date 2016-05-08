package net.roocky.mojian.Widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import net.roocky.mojian.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roocky on 04/23.
 * 天气&背景选择弹窗
 */
public class SelectDialog extends Dialog implements View.OnClickListener {
    private SelectDialog dialog;
    private OnItemClickListener onItemClickListener;
    private int idLayout;

    public SelectDialog(Context context, int themeResId, int idLayout) {
        super(context, themeResId);
        this.idLayout = idLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(idLayout);
        //两个dialog中的item使用相同id
        ImageView ivDialogA = (ImageView)findViewById(R.id.iv_dialog_a);
        ImageView ivDialogB = (ImageView)findViewById(R.id.iv_dialog_b);
        ImageView ivDialogC = (ImageView)findViewById(R.id.iv_dialog_c);
        ImageView ivDialogD = (ImageView)findViewById(R.id.iv_dialog_d);
        ivDialogA.setOnClickListener(this);
        ivDialogB.setOnClickListener(this);
        ivDialogC.setOnClickListener(this);
        ivDialogD.setOnClickListener(this);
        if (idLayout == R.layout.dialog_background) {
            ImageView ivDialogW = (ImageView) findViewById(R.id.iv_dialog_white);
            ImageView ivDialogG = (ImageView) findViewById(R.id.iv_dialog_grey);
            ivDialogW.setOnClickListener(this);
            ivDialogG.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_dialog_a:
                onItemClickListener.onItemClick(dialog, 0);
                break;
            case R.id.iv_dialog_b:
                onItemClickListener.onItemClick(dialog, 1);
                break;
            case R.id.iv_dialog_c:
                onItemClickListener.onItemClick(dialog, 2);
                break;
            case R.id.iv_dialog_d:
                onItemClickListener.onItemClick(dialog, 3);
                break;
            default:        //设置背景时多出的两个（白色&灰色）
                switch (v.getId()) {
                    case R.id.iv_dialog_white:
                        onItemClickListener.onItemClick(dialog, 4);
                        break;
                    case R.id.iv_dialog_grey:
                        onItemClickListener.onItemClick(dialog, 5);
                        break;
                }
                break;
        }
    }

    public void setOnItemClickListener(SelectDialog dialog, OnItemClickListener onItemClickListener) {
        this.dialog = dialog;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(SelectDialog dialog, int position);
    }
}
