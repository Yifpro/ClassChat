package com.example.wyf.classchat.feature.me;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.db.Person;
import com.example.wyf.classchat.util.BitmapUtils;
import com.example.wyf.classchat.db.util.DatabaseUtils;
import com.example.wyf.classchat.util.HeadHelper;
import com.example.wyf.classchat.util.PreferencesUtils;
import com.example.wyf.classchat.util.TintDrawableUtils;
import com.example.wyf.classchat.weight.AutoToolbar;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener, IAppInitContract.IActivity,
        IAppInitContract.IToolbar {

    @BindView(R.id.iv_name)
    ImageView ivName;
    @BindView(R.id.iv_sex)
    ImageView ivSex;
    @BindView(R.id.iv_sign)
    ImageView ivSign;
    @BindView(R.id.iv_xz)
    ImageView ivXz;
    @BindView(R.id.iv_address)
    ImageView ivAddress;
    @BindView(R.id.ll_information)
    LinearLayout mLlInfo;
    @BindView(R.id.et_user_head)
    CircleImageView ivIcon;
    @BindView(R.id.et_username)
    TextInputEditText userName;
    @BindView(R.id.rb_man)
    RadioButton rbMan;
    @BindView(R.id.rb_woman)
    RadioButton rbWoman;
    @BindView(R.id.et_user_sign)
    TextInputEditText userSign;
    @BindView(R.id.spinner_constellation)
    Spinner spinner;
    @BindView(R.id.et_user_address)
    TextInputEditText userAddress;

    private static final String TAG = "UserInfoActivity";
    private final static String NEW_ICON = "head";
    private String id = EMClient.getInstance().getCurrentUser();
    private Bitmap bitmap;
    private String oldName;
    private String oldSign;

    @Override
    public void init() {
        tintDrawable();
        String id = EMClient.getInstance().getCurrentUser();
        Person person = (Person) DatabaseUtils.queryData(FileType.CONTACT, id);
        BitmapUtils.setIcon(this, ivIcon, FileType.CONTACT, id);
        String sex = person.getSex();
        if (sex != null) {
            if (sex.equals("男")) {
                rbMan.setChecked(true);
            } else if (sex.equals("女")) {
                rbWoman.setChecked(true);
            }
        }
        oldName = person.getName();
        oldSign = person.getSign();
        userName.setText(oldName);
        userSign.setText(oldSign);
        userAddress.setText(person.getAddress());
        spinner.setSelection(Integer.parseInt(person.getConstellation()));
    }

    private void tintDrawable() {
        ivName.setBackground(TintDrawableUtils.tintDrawable(ivName.getBackground(),
                getResources().getColorStateList(R.color.setting_detail)));
        ivSex.setBackground(TintDrawableUtils.tintDrawable(ivSex.getBackground(),
                getResources().getColorStateList(R.color.setting_detail)));
        ivSign.setBackground(TintDrawableUtils.tintDrawable(ivSign.getBackground(),
                getResources().getColorStateList(R.color.setting_detail)));
        ivXz.setBackground(TintDrawableUtils.tintDrawable(ivXz.getBackground(),
                getResources().getColorStateList(R.color.setting_detail)));
        ivAddress.setBackground(TintDrawableUtils.tintDrawable(ivAddress.getBackground(),
                getResources().getColorStateList(R.color.setting_detail)));
    }

    @Override
    public String getToolbarTitle() {
        return "编辑资料";
    }

    @Override
    public boolean isShowHome() {
        return false;
    }

    @Subscribe
    public void initToolbar() {
        ((AutoToolbar) findViewById(R.id.toolbar)).setLeftIcon(R.drawable.ic_left_back)
                .setOnLeftIconClickEvent(this)
                .setRightIcon(R.drawable.ic_commit)
                .setOnRightIconClickEvent(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_info;
    }

    //点击edittext外缩回输入法
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            boolean hideInputResult = isShouldHideInput(v, ev);
            if (hideInputResult) {
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) UserInfoActivity.this
                        .getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    //判断点击时是否隐藏软件盘
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getRawX() > left && event.getRawX() < right && event.getRawY() > top && event.getRawY() < bottom);
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mLlInfo.setVisibility(View.GONE);
    }

    @OnClick({R.id.atb_iv_left, R.id.atb_iv_right, R.id.et_user_head, R.id.iv_other, R.id.tv_photo, R.id.tv_album})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.atb_iv_left:
                mLlInfo.setVisibility(View.GONE);
                finish();
                break;
            case R.id.atb_iv_right:
                saveInfo();
                break;
            case R.id.et_user_head:
                mLlInfo.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_other:
                mLlInfo.setVisibility(View.GONE);
                break;
            case R.id.tv_photo:
                //拍照
                mLlInfo.setVisibility(View.GONE);
                HeadHelper.openCamera(this, id, NEW_ICON);
                break;
            case R.id.tv_album:
                //打开图库
                HeadHelper.openAlbum(this);
                mLlInfo.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 保存信息并更新至服务器
     */
    private void saveInfo() {
        if (TextUtils.isEmpty(userName.getText())) {
            userName.setError("昵称不能为空");
            return;
        }

        BitmapUtils.saveIconToLocal(FileType.CONTACT, id, id, bitmap);
        String name = userName.getText().toString().trim();
        String sign = userSign.getText().toString().trim();
        String address = userAddress.getText().toString().trim();
        String constellation = String.valueOf(spinner.getSelectedItemPosition());
        String sex = rbMan.isChecked() || rbWoman.isChecked() ? rbMan.isChecked() ? "男" : "女" : null;

        //更新数据库
        PersonBmob person = new PersonBmob(EMClient.getInstance().getCurrentUser(), name, sex, sign, constellation, address);
        DatabaseUtils.insertPerson(person);

        //更新MeFragment
        if (!name.equals(oldName) || !sign.equals(oldSign) || bitmap != null) {
            EventBus.getDefault().post(new MessageEvent(Constants.REFRESH_PERSONAL_INFO, bitmap));
        }

        //更新bmob
        final PersonBmob personBmob = new PersonBmob(name, sex, sign, constellation, address);
        personBmob.setIcon(bitmap != null ? BitmapUtils.bitmapToString(bitmap) : null);
        personBmob.update(PreferencesUtils.getPreferences().getString(Constants.USER_OBJECT_ID, ""), new UpdateListener() {

            @Override
            public void done(BmobException e) {
                Log.e(TAG, "saveInfo: update success");
            }

        });
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) { //取消设置图片，将图片删除
            HeadHelper.cancel(EMClient.getInstance().getCurrentUser(), NEW_ICON);
        } else if (requestCode == Constants.TAKE_PHOTO) { //拍照回调，进行剪裁
            HeadHelper.cropPicture(this, id, NEW_ICON);
        } else if (requestCode == Constants.TAKE_ALBUM) { //相册回调,进行剪裁
            HeadHelper.cropPhotoByAlbum(this, data.getData(), id);
        } else if (requestCode == Constants.PICTURE_CROP) {
            //拍照剪裁回调，设置头像
            bitmap = BitmapFactory.decodeFile(BitmapUtils.getPath(FileType.CONTACT, id, NEW_ICON));
            ivIcon.setImageBitmap(bitmap);
        } else if (requestCode == Constants.CROP_PHOTO_ALBUM) {
            //相册剪裁回调，设置头像
            bitmap = HeadHelper.getBitmap(data);
            ivIcon.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
