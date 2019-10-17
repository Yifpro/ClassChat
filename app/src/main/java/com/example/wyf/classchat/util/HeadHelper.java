package com.example.wyf.classchat.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.db.util.DatabaseUtils;

import java.io.File;

/**
 * Created by WYF on 2017/10/25.
 */

public class HeadHelper {

    private static HeadHelper helper;

    public HeadHelper() {
        helper = new HeadHelper();
    }

    public static synchronized HeadHelper getInstance() {
        if (helper == null) {
            helper = new HeadHelper();
        }
        return helper;
    }

    /**
     * 打开照相机，兼容7.0
     *
     * @param activity
     */
    public static void openCamera(Activity activity, String id, String fileName) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(BitmapUtils.getPath(FileType.CONTACT, id, fileName));
        //Android7.0以上URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //通过FileProvider创建一个content类型的Uri
            Uri uri = FileProvider.getUriForFile(activity, "com.example.wyf.classchat.fileprovider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            if (hasSdcard()) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            }
        }
        try {
            activity.startActivityForResult(intent, Constants.TAKE_PHOTO);
        } catch (ActivityNotFoundException anf) {
            Toast.makeText(activity, "摄像头尚未准备好", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查是否存在sdcard
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 打开相册
     *
     * @param activity
     */
    public static void openAlbum(Activity activity) {
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(picture, Constants.TAKE_ALBUM);
    }

    /**
     * 拍照后截图
     *
     * @param activity
     * @param fileName
     */
    private static final String TAG = HeadHelper.class.getSimpleName();

    public static void cropPicture(Activity activity, String id, String fileName) {
        File file = new File(BitmapUtils.getPath(FileType.CONTACT, id, fileName));
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri;

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(activity, "com.example.wyf.classchat.fileprovider", file);
            //outputUri不需要ContentUri,否则失败
            //outputUri = FileProvider.getUriForFile(activity, "com.example.wyf.classchat.fileprovider", file);
        } else {
            imageUri = Uri.fromFile(file);
        }
        getCropIntent(intent, imageUri, id, true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, Constants.PICTURE_CROP);
    }

    /**
     * 相册选取图片后截图
     *
     * @param activity
     * @param uri
     */
    public static void cropPhotoByAlbum(Activity activity, Uri uri, String id) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        getCropIntent(intent, uri, id, false);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, Constants.CROP_PHOTO_ALBUM);
    }

    /**
     * 拍照截图回调
     *
     * @param name
     */
    public static Bitmap cameraResult(@FileType.Kind int kind, String id, String name, ImageView iv) {
        Bitmap bitmap = BitmapUtils.compressScale(BitmapFactory.decodeFile(BitmapUtils.getPath(kind, id, name)));
        iv.setImageBitmap(bitmap);
        return bitmap;
    }

    public static Bitmap getBitmap(Intent data) {
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                //return BitmapUtils.compressScale(((Bitmap) bundle.getParcelable("data")));
                return (Bitmap) bundle.getParcelable("data");
            }
        }
        return null;
    }

    private static void getCropIntent(Intent intent, Uri imageUri, String id, boolean isUri) {
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        //设置宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //设置裁剪图片宽高
        intent.putExtra("outputX", 420);
        intent.putExtra("outputY", 420);
        intent.putExtra("scale", true);
        intent.putExtra("noFaceDetection", true);
        if (isUri) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(BitmapUtils.getPath(FileType.CONTACT, id, "head"))));
        }
    }

    public static void cancel(String id, String fileName) {
        File file = new File(BitmapUtils.getPath(FileType.CONTACT, id, fileName));
        if (file.exists()) {
            file.delete();
        }
    }
}
