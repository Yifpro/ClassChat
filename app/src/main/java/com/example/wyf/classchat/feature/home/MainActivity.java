package com.example.wyf.classchat.feature.home;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.feature.contact.ContactFragment;
import com.example.wyf.classchat.feature.me.MeFragment;
import com.example.wyf.classchat.feature.message.MessageFragment;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.util.PreferencesUtils;
import com.example.wyf.classchat.util.TintDrawableUtils;
import com.example.wyf.classchat.weight.PreloadViewPager;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author WYF on 2017/9/26.
 */
public class MainActivity extends AppCompatActivity implements IAppInitContract.IActivity {

    @BindView(R.id.view_pager)
    PreloadViewPager viewPager;
    @BindView(R.id.iv_message)
    ImageView ivMessage;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.iv_contact)
    ImageView ivContact;
    @BindView(R.id.tv_contact)
    TextView tvContact;
    @BindView(R.id.iv_me)
    ImageView ivMe;
    @BindView(R.id.tv_me)
    TextView tvMe;

    private OnBackPressedListener listener;
    private int[] tabId = {R.id.ll_message, R.id.ll_contact, R.id.ll_me};
    private long mExitTime;
    private int currentItem = 0;

    private void initViewPager() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new MessageFragment());
        fragmentList.add(new ContactFragment());
        fragmentList.add(new MeFragment());
        viewPager.setAdapter(new HomePageAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setOnPageChangeListener(new PreloadViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeColor(tabId[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initBottomBar() {
        ivMessage.setBackground(TintDrawableUtils.tintDrawable(ivMessage.getBackground(),
                getResources().getColorStateList(R.color.colorPrimary)));
        tvMessage.setSelected(true);
        ivContact.setBackground(TintDrawableUtils.tintDrawable(ivContact.getBackground(),
                getResources().getColorStateList(R.color.mainBtnNormal)));
        tvContact.setSelected(false);
        ivMe.setBackground(TintDrawableUtils.tintDrawable(ivMe.getBackground(),
                getResources().getColorStateList(R.color.mainBtnNormal)));
        tvMe.setSelected(false);
    }


    @OnClick({R.id.ll_message, R.id.ll_contact, R.id.ll_me})
    public void onClick(View view) {
        changeColor(view.getId());
    }

    private void changeColor(int id) {
        switch (id) {
            case R.id.ll_message:
                if (currentItem == 0) {
                    return;
                }
                setColor(ivMessage, tvMessage, 0);
                break;
            case R.id.ll_contact:
                if (currentItem == 1) {
                    return;
                }
                setColor(ivContact, tvContact, 1);
                break;
            case R.id.ll_me:
                if (currentItem == 2) {
                    return;
                }
                setColor(ivMe, tvMe, 2);
                break;
        }
    }

    private void setColor(View icon, View text, int index) {
        switch (currentItem) {
            case 0:
                ivMessage.setBackground(TintDrawableUtils.tintDrawable(ivMessage.getBackground(),
                        getResources().getColorStateList(R.color.mainBtnNormal)));
                tvMessage.setSelected(false);
                break;
            case 1:
                ivContact.setBackground(TintDrawableUtils.tintDrawable(ivContact.getBackground(),
                        getResources().getColorStateList(R.color.mainBtnNormal)));
                tvContact.setSelected(false);
                break;
            case 2:
                ivMe.setBackground(TintDrawableUtils.tintDrawable(ivMe.getBackground(),
                        getResources().getColorStateList(R.color.mainBtnNormal)));
                tvMe.setSelected(false);
                break;
        }

        icon.setBackground(TintDrawableUtils.tintDrawable(icon.getBackground(),
                getResources().getColorStateList(R.color.colorPrimary)));
        text.setSelected(true);
        viewPager.setCurrentItem(index);
        currentItem = index;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        //存储用户objectId
        HashMap<String, String> map = new HashMap<>();
        map.put("id", EMClient.getInstance().getCurrentUser());
        BmobUtils.getInstance().querryPerson(map, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                PersonBmob personBmob = ((List<PersonBmob>) object).get(0);
                PreferencesUtils.savePreference(Constants.USER_OBJECT_ID, personBmob.getObjectId());
            }
        });
        initViewPager();
        initBottomBar();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    /**
     * 为MeFragment监听返回键点击事件
     */
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 2) {
            if (listener.onBack()) {
                exit();
            }
            return;
        }
        exit();
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        this.listener = listener;
    }

    public interface OnBackPressedListener {
        boolean onBack();
    }

    /**
     * 重按退出
     */
    private void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        PreferencesUtils.savePreference(Constants.LAST_ACCOUNT, EMClient.getInstance().getCurrentUser());
        EMClient.getInstance().logout(true);
        super.onDestroy();
    }
    
}
