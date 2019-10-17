package com.example.wyf.classchat.feature.me.flash;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.IAppInitContract;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author WYF on 2017/10/14.
 */
public class FlashActivity extends AppCompatActivity implements IAppInitContract.IActivity {

    @BindView(R.id.iv_switch)
    ImageView ivSwitch;
    @BindView(R.id.iv_sos)
    ImageView ivSos;
    @BindView(R.id.bg_flash)
    RelativeLayout bgFlash;

    private final static String TAG = FlashActivity.class.getSimpleName();
    private CameraManager manager;
    private Camera mCamera;
    private Drawable[] controlDrawbles;
    private boolean isOpen = true;
    private boolean isSos = false;
    private Thread thread;

    @OnClick({R.id.iv_switch, R.id.iv_sos, R.id.bg_flash})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_switch:
                if (isOpen) {
                    //关闭手电筒
                    ivSwitch.setBackground(controlDrawbles[3]);
                    bgFlash.setBackground(controlDrawbles[1]);
                    closeFlashLight();
                    isOpen = false;
                } else {
                    //开启手电筒
                    if (isSos) {
                        ivSos.setBackground(controlDrawbles[5]);
                        isSos = false;
                    }
                    ivSwitch.setBackground(controlDrawbles[2]);
                    bgFlash.setBackground(controlDrawbles[0]);
                    openFlashLight();
                    isOpen = true;
                }
                break;
            case R.id.iv_sos:
                if (isSos) {
                    //关闭频闪
                    ivSos.setBackground(controlDrawbles[5]);
                    isSos = false;
                } else {
                    //开启频闪
                    ivSos.setBackground(controlDrawbles[4]);
                    fastFlash();
                    isSos = true;
                }
                break;
        }
    }

    private void fastFlash() {
        if (isOpen) {
            ivSwitch.setBackground(controlDrawbles[3]);
            bgFlash.setBackground(controlDrawbles[1]);
            closeFlashLight();
            isOpen = false;
        }
        thread = new Thread() {
            @Override
            public void run() {
                while (isSos) {
                    openFlashLight();
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "run: " + e.getMessage());
                    }
                    closeFlashLight();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "run: " + e.getMessage());
                    }
                }
            }
        };
        thread.start();
    }

    public void closeFlashLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                manager.setTorchMode("0", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }
    }

    private void openFlashLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                manager.setTorchMode("0", true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            final PackageManager pm = getPackageManager();
            final FeatureInfo[] features = pm.getSystemAvailableFeatures();
            for (final FeatureInfo f : features) {
                if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) { // 判断设备是否支持闪光灯
                    if (null == mCamera) {
                        mCamera = Camera.open();
                    }
                    final Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(parameters);
                    mCamera.startPreview();
                }
            }
        }
    }

    @Override
    public void init() {
        Resources resources = getResources();
        controlDrawbles = new Drawable[]{
                resources.getDrawable(R.drawable.bg_flash_light_on),
                resources.getDrawable(R.drawable.bg_flash_light_off),
                resources.getDrawable(R.drawable.ic_flash_light_on),
                resources.getDrawable(R.drawable.ic_flash_light_off),
                resources.getDrawable(R.drawable.ic_sos_on),
                resources.getDrawable(R.drawable.ic_sos_off)};
        //初始化图片状态
        bgFlash.setBackground(controlDrawbles[0]);
        ivSwitch.setBackground(controlDrawbles[2]);
        ivSos.setBackground(controlDrawbles[5]);

        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE); //得到手电筒服务
        //进入时默认打开闪光灯
        openFlashLight();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_falsh;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isOpen) {
            closeFlashLight();
            isOpen = false;
        }
        if (isSos && thread != null) {
            thread.interrupt();
            isSos = false;
        }
    }

}
