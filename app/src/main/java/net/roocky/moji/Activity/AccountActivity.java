package net.roocky.moji.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import net.roocky.moji.R;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by roocky on 03/18.
 * 账号信息
 */
public class AccountActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.sdv_avatar)
    SimpleDraweeView sdvAvatar;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.tv_sex)
    TextView tvSex;
    @Bind(R.id.tv_birthday)
    TextView tvBirthday;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_signature)
    TextView tvSignature;

    private final int TAKE_PHOTO = 0;
    private final int SELECT_PHOTO = 1;
    private final int CROP_PHOTO = 2;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);

        initView();
        setOnClickListener();
    }

    private void initView() {
        preferences = getSharedPreferences("moji", MODE_PRIVATE);
        editor = preferences.edit();
        String uriAvatar = preferences.getString("avatar", null);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.set_account));
        }
        if (uriAvatar != null) {
//            sdvAvatar.setImageURI(Uri.parse(uriAvatar));
        }
    }

    private void setOnClickListener() {
        sdvAvatar.setOnClickListener(this);
    }

    /**
     * View点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sdv_avatar:
                new AlertDialog.Builder(this)
                        .setTitle("更改头像")
                        .setItems(new String[]{"拍照", "从相册中选中"}, this)
                        .show();
                break;
            default:
                break;
        }
    }

    /**
     * 弹窗点击事件
     * @param dialog
     * @param which
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        /**
         * 创建File对象来存储所拍摄的照片
         */

        //返回路径：/storage/emulated/0/Android/包名/files
        File outputImage = new File(getExternalFilesDir(""), "avatar" + System.currentTimeMillis() + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = Uri.fromFile(outputImage);
        switch (which) {
            case TAKE_PHOTO:
                Intent intent_t = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent_t.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent_t, TAKE_PHOTO);       //启动相机Activity
                break;
            case SELECT_PHOTO:
                /**
                 * 设置Action为ACTION_PICK的话可以直接从图库中选取图片，ACTION_GET_CONTENT是从“打开文件”处
                 * 选取，此处选取的图片会跳过截图步骤，具体原因不清楚
                 * Android 6.0系统没有Gallery应用，所以默认打开的是Google Photos，然而似乎Photos并没有裁剪功能
                 */
                Intent intent_s = new Intent(Intent.ACTION_PICK);
                intent_s.setType("image/*");
                intent_s.putExtra("crop", "true");
                intent_s.putExtra("scale", true);
                intent_s.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent_s, CROP_PHOTO);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:        //拍照完成，启动裁剪程序
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO);     //启动裁剪Activity
                }
                break;
            case CROP_PHOTO:        //裁剪完成设置ImageUri
                if (resultCode == RESULT_OK) {
                    sdvAvatar.setImageURI(imageUri);
                    editor.putString("avatar", imageUri.toString()).apply();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
