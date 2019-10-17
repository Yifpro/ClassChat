package com.example.wyf.classchat.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.ClassChatApplication;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/22/022.
 */

public class PermissionManager {

    private PermissionManager() {
    }

    public static void requestPermission(Activity activity, int requestCode, String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        List<String> mRequestList = new ArrayList<>();
        boolean isShow = false;
        for (String permission : permissions) {
            if ((ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED)) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    isShow = true;
                } else {
                    mRequestList.add(permission);
                }
            }
        }
        if (isShow) {
            showTipsDialog(activity);
        }
        if (!mRequestList.isEmpty()) {
            ActivityCompat.requestPermissions(activity, mRequestList.toArray(
                    new String[mRequestList.size()]), requestCode);
        }
    }

    private static void showTipsDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("提示信息")
                .setMessage("当前应用缺少必要权限，相应功能暂时无法使用。如若需要，请单击【确定】按钮前往设置中心进行权限授权。")
                .setNegativeButton("取消", (dialog, which) -> {})
                .setPositiveButton("确定", (dialog, which) -> startAppSettings(activity)).show();
    }

    private static void startAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(intent);
    }
}
