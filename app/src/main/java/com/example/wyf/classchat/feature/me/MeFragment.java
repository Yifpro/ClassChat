package com.example.wyf.classchat.feature.me;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.MeFragmentRecycleAadpter;
import com.example.wyf.classchat.base.BaseFragment;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.db.Person;
import com.example.wyf.classchat.feature.home.MainActivity;
import com.example.wyf.classchat.feature.me.flash.FlashActivity;
import com.example.wyf.classchat.feature.me.setting.SettingsActivity;
import com.example.wyf.classchat.feature.me.weather.WeatherAreaActivity;
import com.example.wyf.classchat.db.util.DatabaseUtils;
import com.example.wyf.classchat.util.BitmapUtils;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author WYF on 2017/9/26.
 */
public class MeFragment extends BaseFragment implements View.OnClickListener, MePageContract.View {

    @BindView(R.id.iv_icon)
    CircleImageView ivIcon;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.tv_sign)
    TextView tvSign;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.iv_qr)
    ImageView ivQr;
    @BindView(R.id.rl_qr)
    RelativeLayout rlQr;

    private MePageContract.Presenter present;

    /**
     * UserInfoActivity回调，更新个人信息
     * @param event 用户信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshInfo(MessageEvent event) {
        if (event.getWhat() == Constants.REFRESH_PERSONAL_INFO) {
            Person p = (Person) DatabaseUtils.queryData(FileType.CONTACT, EMClient.getInstance().getCurrentUser());
            tvTip.setText(p.getName());
            tvSign.setText(p.getSign());
            Bitmap bitmap = (Bitmap) event.getMessage();
            if (bitmap != null) ivIcon.setImageBitmap(bitmap);
        }
    }

    @Override
    public void initData() {
        setPresent(new MePagePresenter(getActivity(), this));
        initRecycleViewData();
        String id = EMClient.getInstance().getCurrentUser();
        Person person = (Person) DatabaseUtils.queryData(FileType.CONTACT, id);
        BitmapUtils.setIcon(mActivity, ivIcon, FileType.CONTACT, id);
        tvTip.setText(person.getName());
        tvSign.setText(person.getSign());
        //设置点击返回键事件
        ((MainActivity)mActivity).setOnBackPressedListener(() -> {
            if (rlQr.getVisibility() != View.GONE) {
                rlQr.setVisibility(View.GONE);
                return false;
            }
            return true;
        });
    }

    private void initRecycleViewData() {
        String[] itemText = getActivity().getResources().getStringArray(R.array.arr_fragment_me_item);
        int[] imags = {R.drawable.ic_weather, R.drawable.ic_flash, R.drawable.ic_qr, R.drawable.ic_setting};//, R.drawable.ic_define_style
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MeFragmentRecycleAadpter adapter = new MeFragmentRecycleAadpter(getActivity(), itemText, imags);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> {
            switch (position) {
                case 0:
                    //startActivity(new Intent(mActivity, WeatherAreaActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(mActivity, FlashActivity.class));
                    break;
                case 2:
                    present.generateQr();
                    break;
                case 3:
                    startActivity(new Intent(mActivity, SettingsActivity.class));
                    break;
            }
        });
    }

    @OnClick({R.id.btn_fab, R.id.rl_qr})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_fab:
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), ivIcon, Constants.TRANSLATE_HEAD);
                ActivityCompat.startActivity(getActivity(), intent, optionsCompat.toBundle());
                break;
            case R.id.rl_qr:
                //隐藏二维码图标
                rlQr.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void displayQr(Bitmap bitmap) {
        rlQr.setVisibility(View.VISIBLE);
        ivQr.setImageBitmap(bitmap);
    }

    /**
     * Fragment切换调用
     * @param isVisibleToUser 可见
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (rlQr != null && rlQr.getVisibility() == View.VISIBLE) {
            rlQr.setVisibility(View.GONE);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public boolean needEventBus() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    public void setPresent(MePageContract.Presenter present) {
        this.present = present;
    }
}
