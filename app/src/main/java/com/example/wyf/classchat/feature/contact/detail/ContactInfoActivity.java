package com.example.wyf.classchat.feature.contact.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.feature.message.chat.ChatActivity;
import com.example.wyf.classchat.util.BitmapUtils;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author WYF on 2017/10/3.
 */
public class ContactInfoActivity extends AppCompatActivity implements IAppInitContract.IActivity, IAppInitContract.ISubscribe,
        ContactInfoContract.View {

    @BindView(R.id.kenBurnsView)
    KenBurnsView kenBurnsView;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_notice_author)
    TextView tvName;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.bottom_sheet)
    RelativeLayout bottomSheet;

    private String userId;
    private ContactInfoAadpter adapter;
    private List<String> list = new ArrayList<>();
    private PersonBmob personBmob;
    private ContactInfoContract.Presenter present;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().post(new MessageEvent(Constants.REFRESH_CONTACT_NEW_INFO, personBmob));
    }

    //从服务器获取联系人最新信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshUi(MessageEvent event) {
        if (event.getWhat() == Constants.GET_DETAIL_INFO_FROM_SERVER) {
            personBmob = (PersonBmob) event.getMessage();
            Glide.with(this).load(new File(BitmapUtils.getPath(FileType.CONTACT, userId, userId))).into(ivIcon);
            tvName.setText(personBmob.getName());
            list.add(personBmob.getId());
            list.add(personBmob.getSign());
            list.add(personBmob.getSex());
            if (personBmob.getConstellation() != null) {
                int index = Integer.parseInt(personBmob.getConstellation());
                list.add(getResources().getStringArray(R.array.arrays_constellation)[index]);
            } else {
                list.add(null);
            }
            list.add(personBmob.getAddress());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setUpdateBg(Bitmap bg) {
        kenBurnsView.setImageBitmap(bg);
    }

    @Override
    public void captureUpdateBgOver(boolean isShouldUpdate) {
        if (isShouldUpdate) {
            present.getNewBg();
        } else {
            File file = new File(BitmapUtils.getPath(FileType.CONTACT, EMClient.getInstance().getCurrentUser(), "contact_bg"));
            if (file.exists()) {
                Glide.with(this).load(file).into(kenBurnsView);
            } else {
                kenBurnsView.setImageResource(R.drawable.bg_contact_info_default);
            }
        }
    }

    @Override
    public void init() {
        setPresent(new ContactInfoPresenter(this, this));
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int[] img = {R.drawable.ic_contact_id, R.drawable.ic_contact_sign, R.drawable.ic_contact_sex,
                R.drawable.ic_contact_xz, R.drawable.ic_contact_address,};
        adapter = new ContactInfoAadpter((ContactInfoActivity.this), img, list);
        recyclerView.setAdapter(adapter);

        userId = getIntent().getStringExtra("userId");
        present.getInfoFromServer(userId);
        present.isUpdateBg();
    }

    @OnClick({R.id.btn_send, R.id.btn_del})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                startActivity(new Intent(ContactInfoActivity.this, ChatActivity.class)
                        .putExtra("userId", userId)
                        .putExtra("type", Constants.CHATTYPE_SINGLE));
                finish();
                break;
            case R.id.btn_del:
                present.deleteContact(userId);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_contact_info;
    }

    @Override
    public void setPresent(ContactInfoContract.Presenter present) {
        this.present = present;
    }

    @Override
    protected void onResume() {
        super.onResume();
        kenBurnsView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        kenBurnsView.pause();
    }
}
