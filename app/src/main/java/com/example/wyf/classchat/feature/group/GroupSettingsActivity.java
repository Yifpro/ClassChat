package com.example.wyf.classchat.feature.group;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.SettRvAdapter;
import com.example.wyf.classchat.adapter.decoration.MyItemDecoration;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.GroupBmob;
import com.example.wyf.classchat.bean.GroupSettingItemView;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.feature.group.notice.NoticeActivity;
import com.example.wyf.classchat.feature.group.rollcall.RollCallActivity;
import com.example.wyf.classchat.feature.group.vote.VoteActivity;
import com.example.wyf.classchat.feature.group.rollcall.RollCallListFragment;
import com.example.wyf.classchat.feature.group.rollcall.StartRollCallFragment;
import com.example.wyf.classchat.feature.group.clazz.ClazzActivity;
import com.example.wyf.classchat.feature.group.file.ShareFileActivity;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BitmapUtils;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.util.HeadHelper;
import com.example.wyf.classchat.util.ProgressUtils;
import com.example.wyf.classchat.weight.AutoToolbar;
import com.example.wyf.classchat.weight.MyScrollView;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class GroupSettingsActivity extends AppCompatActivity implements GroupSettingsContract.View,
        IAppInitContract.IActivity, IAppInitContract.IToolbar {

    @BindView(R.id.iv_setting_icon)
    ImageView ivSettingIcon;
    @BindView(R.id.tv_group_id)
    TextView tvGroupId;
    @BindView(R.id.tv_group_name)
    TextView tvGroupName;
    @BindView(R.id.rv_setting)
    RecyclerView recyclerView;
    @BindView(R.id.tv_notice_read_count)
    TextView tvGroupCount;
    @BindView(R.id.group_list)
    LinearLayout groupList;
    @BindView(R.id.tv_add_admin)
    TextView tvAddAdmin;
    @BindView(R.id.btn_group_exit)
    Button btnGroupExit;
    @BindView(R.id.sv)
    MyScrollView scrollView;
    @BindView(R.id.toolbar)
    AutoToolbar toolbar;
    
    private final static String GROUP_HEAD_IMAGE = "group_icon";
    private GroupSettingsContract.Presenter present;
    private Bitmap bitmap;
    private boolean isTransparent;
    private boolean isIconSetted;
    private String groupId;
    private Boolean isOwner = false;
    private ArrayList<String> admins;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            EventBus.getDefault().post(new MessageEvent(Constants.REFRESH_GROUP_ICON, groupId));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("选择头像");
        menu.add(0, 0, Menu.NONE, "拍照");
        menu.add(0, 1, Menu.NONE, "相册");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                HeadHelper.openCamera(GroupSettingsActivity.this, groupId, GROUP_HEAD_IMAGE);
                break;
            case 1:
                HeadHelper.openAlbum(this);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    @Override
    public void setGroupCount(int count) {
        tvGroupCount.setText(count + "名成员");
    }

    @Override
    public void init() {
        ProgressUtils.showProgress(this, "加载中...");
        groupId = getIntent().getStringExtra("userId");
        setPresent(new GroupSettingsPresenter(this, this));
        toolbar.setVisibility(View.INVISIBLE);
        //注册上下文菜单
        registerForContextMenu(ivSettingIcon);
        initGroup();
        initRecyclerView();
        initListener();
        //获取管理员列表
        present.getAdminList(groupId);
    }

    private void initListener() {
        scrollView.setScrollViewListener((scrollView, x, y, oldx, oldy) -> {
            if (y > 30 && isTransparent) {
                isTransparent = false;
                setTran();
            } else if (y < 30 && !isTransparent) {
                isTransparent = true;
                setTran();
            }
        });
    }

    private void initGroup() {
        //初始化群名，id，图片
        tvGroupId.setText(groupId);
        tvGroupName.setText(EMClient.getInstance().groupManager().getGroup(groupId).getGroupName());
        present.getGroupCount(groupId);
        BitmapUtils.setIcon(this, ivSettingIcon, FileType.GROUP, groupId);
    }

    @Override
    public void getAdminListSuccess(ArrayList<String> admins) {
        ProgressUtils.dismiss();
        this.admins = admins;
        isOwner = EMClient.getInstance().getCurrentUser().equals(admins.get(0));
    }

    private void initRecyclerView() {
        ArrayList<GroupSettingItemView> list = new ArrayList<>();
        String[] items = getResources().getStringArray(R.array.arr_activity_setttings_recycle_item);
        int[] imgs = {R.drawable.ic_group_func_anoce, R.drawable.ic_group_func_file, R.drawable.ic_group_func_register, R.drawable.ic_group_func_rat,
                R.drawable.ic_group_func_pic,
                R.drawable.ic_group_func_cal,
                R.drawable.ic_group_func_clazz,
                R.drawable.ic_group_func_more};
        for (int i = 0; i < items.length; i++) {
            GroupSettingItemView groupItem = new GroupSettingItemView(imgs[i], items[i]);
            list.add(groupItem);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(GroupSettingsActivity.this, 4));
        recyclerView.addItemDecoration(new MyItemDecoration(30));
        SettRvAdapter adapter = new SettRvAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> {
            switch (position) {
                case 0:
                    //公告
                    goToActivity(NoticeActivity.class);
                    break;
                case 1:
                    //文件
                    goToActivity(ShareFileActivity.class);
                    break;
                case 2:
                    //点名
                    goToActivity(RollCallActivity.class);
                    break;
                case 3:
                    //投票
                    goToActivity(VoteActivity.class);
                    break;
                case 4:

                    break;
                case 5:

                    break;
                case 6:
                    goToActivity(ClazzActivity.class);
                    break;
                case 7:

                    break;
            }
        });
    }

    private void goToActivity(Class clazz) {
        startActivity(new Intent(GroupSettingsActivity.this, clazz)
                .putExtra(Constants.GROUP_ID, groupId)
                .putStringArrayListExtra(Constants.GROUP_ADMIN_LIST, admins));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        StartRollCallFragment start_register_fragment = (StartRollCallFragment) fm.findFragmentByTag("start_register_fragment");
        RollCallListFragment register_member_list_fragment = (RollCallListFragment) fm.findFragmentByTag("register_member_list_fragment");
        if (register_member_list_fragment != null) {
            if (register_member_list_fragment.isVisible()) {
                hideCurrent(register_member_list_fragment);
                return;
            }
        }
        if (start_register_fragment != null) {
            if (start_register_fragment.isVisible()) {
                hideCurrent(start_register_fragment);
                return;
            }
        }
        //退出时保存头像
        if (isIconSetted) {
            present.saveGroupIcon(groupId, bitmap);
        }
        super.onBackPressed();
    }

    private void hideCurrent(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(fragment);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("test", "onActivityResult: gohere");
        //取消设置图片，将图片删除
        if (resultCode == RESULT_CANCELED) {
            HeadHelper.cancel(groupId, GROUP_HEAD_IMAGE);
        } else if (requestCode == Constants.TAKE_PHOTO) { //拍照回调
            isIconSetted = true;
            ivSettingIcon.setImageBitmap((Bitmap) data.getExtras().get("data"));
        } else if (requestCode == Constants.TAKE_ALBUM) { //相册回调
            isIconSetted = true;
            Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //获取照片
            bitmap = BitmapFactory.decodeFile(cursor.getString(columnIndex));
            cursor.close();
            ivSettingIcon.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.tv_add_admin, R.id.group_list, R.id.btn_group_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add_admin:
                if (isOwner) {
                    showAddManager();
                } else {
                    Toast.makeText(this, "暂无权限任命管理员", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.group_list: //群成员
                goToActivity(GroupMembersActivity.class);
                break;
            case R.id.btn_group_exit:
                if (isOwner) {
                    present.dismissGroup(groupId);
                } else {
                    present.exitGroup(groupId);
                }
                break;
        }
    }

    private void showAddManager() {
        final EditText managerId = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("任命管理员")
                .setIcon(R.drawable.ic_appiont_manager)
                .setView(managerId)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", (dialog, which) -> {
                    final String adminId = managerId.getText().toString();
                    if (adminId.equals(EMClient.getInstance().getCurrentUser())) {
                        Toast.makeText(GroupSettingsActivity.this, "不能任命自己为管理员", Toast.LENGTH_SHORT).show();
                    }
                    present.addManager(groupId, adminId);
                });
        builder.show();
    }

    @Override
    public void addManagerSuccess() {
        Toast.makeText(GroupSettingsActivity.this, "任命管理员成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismissSuccess() {
        Toast.makeText(GroupSettingsActivity.this, "解散成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void exitSuccess() {
        Toast.makeText(this, "退群成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getToolbarTitle() {
        return "圈聊资料";
    }

    @Override
    public boolean isShowHome() {
        return true;
    }

    @Override
    public void initToolbar() {

    }

    private void setTran() {
        if (isTransparent) {
            ObjectAnimator.ofFloat(toolbar, "alpha", 1, 0).setDuration(400).start();
        } else {
            if (!toolbar.isShown()) toolbar.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(toolbar, "alpha", 0, 1).setDuration(400).start();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    public void setPresent(GroupSettingsContract.Presenter present) {
        this.present = present;
    }
}
