package net.roocky.moji.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import net.roocky.moji.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by roocky on 03/18.
 * 账号信息
 */
public class AccountActivity extends AppCompatActivity implements View.OnClickListener {
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
    private final int CROP_PHOTO = 2;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);

        setOnClickListener();
    }

    private void setOnClickListener() {
        sdvAvatar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sdv_avatar:
                new AlertDialog.Builder(this)
                        .setTitle("更改头像")
                        .setItems(new String[]{"拍照", "从相册中选中"},
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                //创建File对象来存储所拍摄的照片
                                                File outputImage = new File(Environment.getExternalStorageDirectory(), "avatar.jpg");
                                                try {
                                                    if (outputImage.exists()) {
                                                        outputImage.delete();
                                                    }
                                                    outputImage.createNewFile();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                imageUri = Uri.fromFile(outputImage);
                                                Intent intent = new Intent("android.media.action. IMAGE_CAPTURE");
                                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                                startActivityForResult(intent, TAKE_PHOTO);
                                                break;
                                            case 1:

                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                })
                        .show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO);
                }
                break;
            case 1:

                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        sdvAvatar.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
}
