package com.example.wyf.classchat.util;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by WYF on 2017/9/23.
 */

public class ProgressUtils {

    private static ProgressDialog progressDialog;

    public static void showProgress(Activity activity, String message) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void dismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
