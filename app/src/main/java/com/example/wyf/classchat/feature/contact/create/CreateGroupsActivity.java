package com.example.wyf.classchat.feature.contact.create;

import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.util.ForbidFastClickUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author WYF on 2017/9/27.
 */
public class CreateGroupsActivity extends AppCompatActivity implements IAppInitContract.IActivity, IAppInitContract.IToolbar,
        CreateGroupsContract.View {

    @BindView(R.id.et_group_name)
    EditText groupName;
    @BindView(R.id.et_group_desc)
    EditText groupDesc;
    @BindView(R.id.btn_rollcall_commit)
    Button commit;

    private CreateGroupsContract.Presenter present;

    @Override
    public void onCreateSuccess() {
        Toast.makeText(this, "创建群成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void init() {
        setPresent(new CreateGroupsPresenter(this, this));
    }

    @OnClick(R.id.btn_rollcall_commit)
    public void onClick() {
        present.createGroup(groupName, groupDesc);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_group;
    }

    @Override
    public void initToolbar() {
    }

    @Override
    public String getToolbarTitle() {
        return "创建群";
    }

    @Override
    public boolean isShowHome() {
        return true;
    }

    @Override
    public void setPresent(CreateGroupsContract.Presenter present) {
        this.present = present;
    }
}
