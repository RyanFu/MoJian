package net.roocky.mojian.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;

/**
 * Created by roocky on 04/16.
 *
 */
public abstract class BaseActivity extends AppCompatActivity implements
        DialogInterface.OnClickListener {
    protected SharedPreferences preferences;
    protected SharedPreferences.Editor editor;

    protected Uri imageUri;

    protected final int TAKE_PHOTO = 0;
    protected final int SELECT_PHOTO = 1;
    protected final int SET_DEFAULT = 2;
    protected final int PATTERN_LOCK_DIARY = 3;
    protected final int PATTERN_LOCK_SET = 4;
    protected final int PATTERN_LOCK_CLEAR = 5;
    protected final int FRAGMENT_NOTE = 0;
    protected final int FRAGMENT_DIARY = 1;
    protected final int FRAGMENT_SETTING = 2;


    protected int fragmentId = 0;         //记录当前所在的Fragment
    protected int setWhat;          //用来判断当前的Activity
    protected int BACKGROUND = 0;
    protected int AVATAR = 1;

    protected AlertDialog dialogAvatar;
    protected AlertDialog dialogBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("mojian", MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * 弹窗点击事件
     *
     * @param dialog
     * @param which
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (dialog.equals(dialogAvatar) || dialog.equals(dialogBackground)) {
            /**
             * 创建File对象来存储所拍摄的照片
             */

            String picName;
            if (setWhat == AVATAR) {
                picName = "avatar";
            } else {
                picName = "background";
            }
            //返回路径：/storage/emulated/0/Android/包名/files
            File outputImage = new File(getExternalFilesDir(""), picName + System.currentTimeMillis() + ".jpg");
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
                //启动相机Activity，resultCode == TAKE_PHOTO
                case TAKE_PHOTO:
                    Intent intent_t = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent_t.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent_t, TAKE_PHOTO);
                    break;

                //启动文件选择器，resultCode == Crop.REQUEST_PICK
                case SELECT_PHOTO:
                    /**
                     * 设置Action为ACTION_PICK的话可以直接从图库中选取图片，ACTION_GET_CONTENT是从“打开文件”处
                     * 选取，此处选取的图片会跳过截图步骤，具体原因不清楚
                     * Android 6.0系统没有Gallery应用，所以默认打开的是Google Photos，然而似乎Photos并没有裁剪功能
                     *
                     * 此处调用第三方裁剪库android-crop实现选择&裁剪图片（选择图片基于原生的选择器）
                     */
                    Crop.pickImage(this);
                    break;
                case SET_DEFAULT:       //恢复默认背景
                    editor.putString("background" + fragmentId, "").commit();
                    setBackground(Uri.parse(""));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //拍照完成，启动裁剪程序
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (setWhat == AVATAR) {
                        Crop.of(imageUri, imageUri).withMaxSize(300, 300)     //第一个参数imageUri是拍照生成的原始图片Uri
                                .asSquare().start(this);                      //第二个参数imageUri是裁剪完成生成的图片Uri
                    } else {
                        Crop.of(imageUri, imageUri).withMaxSize(540, 360).withAspect(3, 2).start(this);
                    }
                }
                break;

            //选择完成，启动裁剪程序
            case Crop.REQUEST_PICK:
                if (resultCode == RESULT_OK && data != null) {
                    if (setWhat == AVATAR) {
                        Crop.of(data.getData(), imageUri).withMaxSize(300, 300)   //data.getData()为选择图片后得到的Uri
                                .asSquare().start(this);                          //resultCode == Crop.REQUEST_CROP
                    } else {
                        Crop.of(data.getData(), imageUri).withMaxSize(540, 360).withAspect(3, 2).start(this);
                    }
                }
                break;

            //裁剪完成，设置图片Uri
            case Crop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    if (setWhat == AVATAR) {
                        setAvatar(imageUri);        //设置头像
                        editor.putString("avatar", imageUri.toString()).apply();
                    } else {
                        setBackground(imageUri);
                        editor.putString("background" + fragmentId, imageUri.toString()).apply();
                    }
                }
                break;
            default:
                break;
        }
    }

    protected abstract void setAvatar(Uri imageUri);
    protected abstract void setBackground(Uri imageUri);
}
