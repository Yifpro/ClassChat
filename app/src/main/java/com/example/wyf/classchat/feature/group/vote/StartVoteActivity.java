package com.example.wyf.classchat.feature.group.vote;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.VoteBean;
import com.example.wyf.classchat.bean.VoteResult;
import com.example.wyf.classchat.util.ForbidFastClickUtils;
import com.example.wyf.classchat.weight.AutoToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by gsh on 2017/10/10.
 */

public class StartVoteActivity extends AppCompatActivity implements View.OnClickListener, IAppInitContract.IActivity, IAppInitContract.IToolbar {

    @BindView(R.id.toolbar)
    AutoToolbar toolbar;
    @BindView(R.id.vote_title)
    EditText voteTitle;
    @BindView(R.id.vote_content)
    EditText voteContent;
    @BindView(R.id.rb_singlevote)
    RadioButton singleButton;
    @BindView(R.id.rb_mulpiltevote)
    RadioButton rbMulpiltevote;
    @BindView(R.id.vote_chose)
    TextView voteChose;
    @BindView(R.id.list_person)
    TextView listPerson;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private int Option3 = 0;
    private int Option4 = 0;
    private int Option5 = 0;
    private int Option6 = 0;
    private int Option7 = 0;
    private int Option8 = 0;
    private int Option9 = 0;
    private int Option10 = 0;
    private int Option11 = 0;
    private int Option12 = 0;
    private int Option13 = 0;
    private int Option14 = 0;
    private int Option15 = 0;
    private ArrayList<Integer> optionList = new ArrayList<>();

    private String vote_Content;
    private String vote_Option1;
    private String vote_Option2;

    private String userId;
    private boolean isSingle;
    private String timeDate;
    private ArrayList<String> arrayOptionContent = new ArrayList<String>();
    private int OPTION = 0;
    private String groupId;
    private String vote_Title;
    private ArrayList<String> checkName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_startvote;
    }

    @Override
    public String getToolbarTitle() {
        return "发起评优";
    }

    @Override
    public boolean isShowHome() {
        return true;
    }

    public void initToolbar() {
        toolbar.setRightText("发布").setOnRightTextClickEvent(this);
    }

    @Override
    public void init() {
        optionList.add(Option3);
        optionList.add(Option4);
        optionList.add(Option5);
        optionList.add(Option6);
        optionList.add(Option7);
        optionList.add(Option8);
        optionList.add(Option9);
        optionList.add(Option10);
        optionList.add(Option11);
        optionList.add(Option12);
        optionList.add(Option13);
        optionList.add(Option14);
        optionList.add(Option15);
        initToolbar();

        userId = getIntent().getStringExtra("userId");
        groupId = getIntent().getStringExtra("groupId");
    }


    private void openActivity() {
        getData();
        if (vote_Content.isEmpty() || vote_Title.isEmpty()) {
            Toast.makeText(this, "关键字不能为空，请重新输入", Toast.LENGTH_SHORT).show();

        } else {
            try {
                upLoadData();
            } catch (Exception e) {
                Toast.makeText(this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
            }

        }


    }

    private void upLoadData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final VoteBean vb = new VoteBean();
                vb.setSingle(isSingle);
                vb.setUserId(userId);
                vb.setTitle(vote_Title);
                vb.setVoteOption1(vote_Option1);
                vb.setVoteOption2(vote_Option2);
                vb.setOptions(arrayOptionContent);
                vb.setOPTION(OPTION);
                vb.setCreatTime(timeDate);
                vb.setVoteContent(vote_Content);
                vb.setUserId(userId);
                vb.setgroupId(groupId);
                vb.setState("doing");
                vb.save(new SaveListener<String>() {

                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            //Toast.makeText(StartVoteActivity.this, "chenggongla", Toast.LENGTH_SHORT).show();
                            upLoadResult();
                        } else {
                            Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());

                        }
                    }
                });


            }

            private void upLoadResult() {
                final VoteResult vr = new VoteResult();
                vr.setVote_content(vote_Content);
                vr.setList(optionList);
                vr.setgroupId(groupId);
                ArrayList<String> array = new ArrayList<String>();
                array.add("a");
                vr.setDoneUser(array);
                vr.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            Toast.makeText(StartVoteActivity.this, "发起评优成功", Toast.LENGTH_SHORT).show();
                            Log.i("save checkname", checkName + "");
                            finish();
                        } else {
                            Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());

                        }
                    }
                });
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vote_chose:
                Intent intent = new Intent(StartVoteActivity.this, VoteChoosePreson.class);
                intent.putExtra("groupId", groupId);
                startActivityForResult(intent, 10086);
                break;
            case R.id.atb_iv_right:
                if (ForbidFastClickUtils.isFastClick()) {
                    if (checkName != null) {
                        openActivity();

                    } else {
                        Toast.makeText(this, "请选择候选人", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10086) {
            if (data != null) {
                checkName = data.getStringArrayListExtra("checkName");
                Log.i("get checkName", checkName + "");
            }
            if (checkName != null) {
                listPerson.setVisibility(View.VISIBLE);
                final StartVoteAdapter startVoteAdapter = new StartVoteAdapter(StartVoteActivity.this, checkName);
                recyclerView.setAdapter(startVoteAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(StartVoteActivity.this));
                startVoteAdapter.setMdelListener(new StartVoteAdapter.delListener() {
                    @Override
                    public void delItem(int position) {
                        if (checkName.size() <= 2) {
                            Toast.makeText(StartVoteActivity.this, "无法继续删除候选人", Toast.LENGTH_SHORT).show();
                        } else {
                            checkName.remove(position);
                            startVoteAdapter.notifyItemRemoved(position);
                        }

                    }
                });
            } else {
                listPerson.setVisibility(View.GONE);
            }
            Log.e("startvote", checkName + "");
        }
    }

    private void getData() {
        vote_Title = voteTitle.getText().toString().trim();
        vote_Content = voteContent.getText().toString().trim();
        if (checkName != null) {
            vote_Option1 = checkName.get(0);
            vote_Option2 = checkName.get(1);
            OPTION = checkName.size() - 2;
        }
        if (OPTION > 0) {
            for (int i = 0; i < OPTION; i++) {
                String name = checkName.get(i + 2);
                arrayOptionContent.add(name);
                Log.i("get data check", checkName + "");
            }
        }
        isSingle = singleButton.isChecked();


        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd    hh:mm:ss");
        timeDate = sDateFormat.format(new Date());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    static class StartVoteAdapter extends RecyclerView.Adapter<StartVoteAdapter.ViewHolder> {

        private final Context mContext;
        private ArrayList<String> data;

        public StartVoteAdapter(Context mContext, ArrayList<String> checkName) {
            this.mContext = mContext;
            this.data = checkName;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mContext, R.layout.startvote_item, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.tv_number.setText(data.get(position));

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView tv_number;
            private ImageView delPerson;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_number = itemView.findViewById(R.id.item_text);
                delPerson = itemView.findViewById(R.id.startvote_remove);
                delPerson.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (mdelListener != null) {
                    mdelListener.delItem(getLayoutPosition());
                }
            }
        }

        public static interface delListener {
            void delItem(int position);
        }

        private delListener mdelListener = null;

        public void setMdelListener(delListener mdelListener) {
            this.mdelListener = mdelListener;
        }
    }

}
