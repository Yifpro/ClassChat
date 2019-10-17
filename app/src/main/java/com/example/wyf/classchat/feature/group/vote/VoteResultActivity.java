package com.example.wyf.classchat.feature.group.vote;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.VoteBean;
import com.example.wyf.classchat.bean.VoteResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by gsh on 2017/10/14.
 */

public class VoteResultActivity extends AppCompatActivity implements IAppInitContract.IActivity {
    @BindView(R.id.voteresult_title)
    TextView title;
    @BindView(R.id.voteresult_content)
    TextView result_content;
    @BindView(R.id.vote_content1)
    TextView mTextView1;
    @BindView(R.id.vote_result1)
    TextView resultText1;
    @BindView(R.id.ll_1)
    LinearLayout ll1;
    @BindView(R.id.vote_content2)
    TextView mTextView2;
    @BindView(R.id.vote_result2)
    TextView resultText2;
    @BindView(R.id.ll_2)
    LinearLayout ll2;
    @BindView(R.id.vote_content3)
    TextView mTextView3;
    @BindView(R.id.vote_result3)
    TextView resultText3;
    @BindView(R.id.ll_3)
    LinearLayout ll3;
    @BindView(R.id.vote_content4)
    TextView mTextView4;
    @BindView(R.id.vote_result4)
    TextView resultText4;
    @BindView(R.id.ll_4)
    LinearLayout ll4;
    @BindView(R.id.vote_content5)
    TextView mTextView5;
    @BindView(R.id.vote_result5)
    TextView resultText5;
    @BindView(R.id.ll_5)
    LinearLayout ll5;
    @BindView(R.id.vote_content6)
    TextView mTextView6;
    @BindView(R.id.vote_result6)
    TextView resultText6;
    @BindView(R.id.ll_6)
    LinearLayout ll6;
    @BindView(R.id.vote_content7)
    TextView mTextView7;
    @BindView(R.id.vote_result7)
    TextView resultText7;
    @BindView(R.id.ll_7)
    LinearLayout ll7;
    @BindView(R.id.vote_content8)
    TextView mTextView8;
    @BindView(R.id.vote_result8)
    TextView resultText8;
    @BindView(R.id.ll_8)
    LinearLayout ll8;
    @BindView(R.id.vote_content9)
    TextView mTextView9;
    @BindView(R.id.vote_result9)
    TextView resultText9;
    @BindView(R.id.ll_9)
    LinearLayout ll9;
    @BindView(R.id.vote_content10)
    TextView mTextView10;
    @BindView(R.id.vote_result10)
    TextView resultText10;
    @BindView(R.id.ll_10)
    LinearLayout ll10;
    @BindView(R.id.vote_content11)
    TextView mTextView11;
    @BindView(R.id.vote_result11)
    TextView resultText11;
    @BindView(R.id.ll_11)
    LinearLayout ll11;
    @BindView(R.id.vote_content12)
    TextView mTextView12;
    @BindView(R.id.vote_result12)
    TextView resultText12;
    @BindView(R.id.ll_12)
    LinearLayout ll12;
    @BindView(R.id.vote_content13)
    TextView mTextView13;
    @BindView(R.id.vote_result13)
    TextView resultText13;
    @BindView(R.id.ll_13)
    LinearLayout ll13;
    @BindView(R.id.vote_content14)
    TextView mTextView14;
    @BindView(R.id.vote_result14)
    TextView resultText14;
    @BindView(R.id.ll_14)
    LinearLayout ll14;
    @BindView(R.id.vote_content15)
    TextView mTextView15;
    @BindView(R.id.vote_result15)
    TextView resultText15;
    @BindView(R.id.ll_15)
    LinearLayout ll15;
    private String state;
    private int votereuslt1;
    private int votereuslt2;
    private String voteOption1;
    private String voteOption2;
    private ArrayList<String> options;
    private List<VoteResult> resultList;
    private List<VoteBean> voteList;
    private int OPTION;
    private int position;
    private String objectId;
    private int getOption1;
    private int getOption2;
    private ArrayList<TextView> textContent = new ArrayList<>();
    private ArrayList<TextView> result = new ArrayList<>();
    private ArrayList<LinearLayout> ll = new ArrayList<>();
    private ArrayList<String> doneUser;
    private String userId;
    private String groupId;
    private String voteObjectId;
    private String resultObjectId;
    private String vote_content;
    private String vote_title;
    private Boolean haveDone = false;
    private ArrayList<Integer> list1;

    @Override
    public int getLayoutId() {
        return R.layout.vote_resultactivity;
    }

    @Override
    public void init() {
        textContent.add(mTextView3);
        textContent.add(mTextView4);
        textContent.add(mTextView5);
        textContent.add(mTextView6);
        textContent.add(mTextView7);
        textContent.add(mTextView8);
        textContent.add(mTextView9);
        textContent.add(mTextView10);
        textContent.add(mTextView11);
        textContent.add(mTextView12);
        textContent.add(mTextView13);
        textContent.add(mTextView14);
        textContent.add(mTextView15);

        result.add(resultText3);
        result.add(resultText4);
        result.add(resultText5);
        result.add(resultText6);
        result.add(resultText7);
        result.add(resultText8);
        result.add(resultText9);
        result.add(resultText10);
        result.add(resultText11);
        result.add(resultText12);
        result.add(resultText13);
        result.add(resultText14);
        result.add(resultText15);

        ll.add(ll3);
        ll.add(ll4);
        ll.add(ll5);
        ll.add(ll6);
        ll.add(ll7);
        ll.add(ll8);
        ll.add(ll9);
        ll.add(ll10);
        ll.add(ll11);
        ll.add(ll12);
        ll.add(ll13);
        ll.add(ll14);
        ll.add(ll15);

        getDataFormBomb();
    }

    private void uploadDatas() {
        VoteResult vResutl = new VoteResult();


        int singleCheckOption = getIntent().getIntExtra("singleCheckOption", 0);
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < doneUser.size(); i++) {
            arrayList.add(doneUser.get(i));
        }
        arrayList.add(userId);
        vResutl.setDoneUser(arrayList);


        if (singleCheckOption == 1) {
            getOption1 = votereuslt1 + 1;
            getOption2 = votereuslt2;
            Log.i("getOption1", votereuslt1 + "");
            vResutl.setOption1(getOption1);
            vResutl.setOption2(votereuslt2);
            vResutl.setList(list1);
            Log.i("upvotereuslt1", getOption1 + "");
            Log.i("upvotereuslt2", votereuslt2 + "");
            Log.i("uplist", list1 + "");
        } else if (singleCheckOption == 2) {
            getOption2 = votereuslt2 + 1;
            getOption1 = votereuslt1;
            vResutl.setOption2(getOption2);
            vResutl.setOption1(votereuslt1);
            vResutl.setList(list1);
            Log.i("upvotereuslt1", votereuslt1 + "");
            Log.i("upvotereuslt2", getOption2 + "");
            Log.i("uplist", list1 + "");

        } else {
            getOption2 = votereuslt2;
            getOption1 = votereuslt1;
            int i = list1.get(singleCheckOption - 3) + 1;
            list1.remove(singleCheckOption - 3);
            list1.add(singleCheckOption - 3, i);
            vResutl.setOption2(votereuslt2);
            vResutl.setOption1(votereuslt1);
            vResutl.setList(list1);
            Log.i("upvotereuslt1", votereuslt1 + "");
            Log.i("upvotereuslt2", votereuslt2 + "");
            Log.i("uplist", list1 + "");
        }

        vResutl.update(resultObjectId, new UpdateListener() {


            public void done(BmobException e) {
                if (e == null) {
                    Log.i("bmobresult", "更新成功");
                    setDatas();
                } else {
                    Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    private void setDatas() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                title.setText("标题   " + vote_title);

                result_content.setText("内容   " + vote_content);
                mTextView1.setText(voteOption1);
                mTextView2.setText(voteOption2);
                resultText1.setText(getOption1 + "票");
                resultText2.setText(getOption2 + "票");
                Log.i("option", OPTION + "");
                for (int i = 0; i < OPTION; i++) {
                    ll.get(i).setVisibility(View.VISIBLE);

                    result.get(i).setText(resultList.get(position).getList().get(i) + "票");
                    textContent.get(i).setText(voteList.get(position).getOptions().get(i));
                }
            }
        });
    }

    private void getDataFormBomb() {

        //哪个条目
        position = 0;

        userId = getIntent().getStringExtra("userId");
        groupId = getIntent().getStringExtra("groupId");
        voteObjectId = getIntent().getStringExtra("voteObjectId");
        resultObjectId = getIntent().getStringExtra("resultObjectId");

        querryVoteResult();


    }


    private void querryVoteResult() {
        BmobQuery<VoteResult> resultQuery = new BmobQuery<VoteResult>();
        resultQuery.addWhereEqualTo("objectId", resultObjectId);
        resultQuery.findObjects(new FindListener<VoteResult>() {
            @Override
            public void done(List<VoteResult> list, BmobException e) {
                if (e == null) {
                    querryVoteBean(list);
                }
            }
        });
    }


    private void querryVoteBean(final List<VoteResult> list) {
        BmobQuery<VoteBean> query = new BmobQuery<VoteBean>();
        query.addWhereEqualTo("objectId", voteObjectId);
        // query.addQueryKeys(voteObjectId);
//执行查询方法
        query.findObjects(new FindListener<VoteBean>() {
            @Override
            public void done(List<VoteBean> object, BmobException e) {
                if (e == null) {
                    Log.i("position", position + "");
                    resultList = list;
                    objectId = resultList.get(position).getObjectId();
                    list1 = resultList.get(position).getList();
                    // doneUser = resultList.get(position).getDoneUser();
                    votereuslt1 = resultList.get(position).getOption1();
                    Log.i("getvotereuslt1", votereuslt1 + "");


                    votereuslt2 = resultList.get(position).getOption2();
                    Log.i("getvotereuslt2", votereuslt2 + "");
                    Log.i("getresultlist", resultList.get(position).getList() + "");
                    doneUser = resultList.get(position).getDoneUser();
                    Log.i("doneUser", doneUser + "");
                    voteList = object;

                    voteList.get(position).getOptions();
                    OPTION = voteList.get(position).getOPTION();
                    state = voteList.get(position).getState();
                    voteOption1 = voteList.get(position).getVoteOption1();
                    voteOption2 = voteList.get(position).getVoteOption2();
                    vote_content = voteList.get(position).getVoteContent();
                    vote_title = voteList.get(position).getTitle();
                    Log.i("voteOption2", voteOption2);
                    Log.i("voteOption1", voteOption1);
                    if (OPTION > 0) {
                        options = voteList.get(position).getOptions();
                    }
                    Log.i("doneUser", doneUser.size() + "");
                    // uploadDatas();
                    ifDone();

                } else {
                    Log.i("bmob", "失败voteresultacitivity：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    private void ifDone() {
        if (doneUser.size() > 1) {
            for (int i = 0; i < doneUser.size(); i++) {

                if (doneUser.contains(userId) || state.equals("done")) {

                    haveDone = true;


                }
            }
            haveDone(haveDone);
        } else {
            uploadDatas();
            Log.i("ifdone2", "ssss");
        }
    }

    private void haveDone(Boolean haveDone) {
        if (haveDone) {
            Toast.makeText(VoteResultActivity.this, "你已经投过票了", Toast.LENGTH_SHORT).show();

            showDatas();
        } else {
            uploadDatas();
        }

    }

    private void showDatas() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title.setText("标题   " + vote_title);

                result_content.setText("内容   " + vote_content);
                mTextView1.setText(voteOption1);
                mTextView2.setText(voteOption2);
                Log.i("votereuslt1", votereuslt1 + "");
                Log.i("votereuslt2", votereuslt2 + "");
                resultText1.setText(votereuslt1 + "票");
                resultText2.setText(votereuslt2 + "票");
                Log.i("option", OPTION + "");
                for (int i = 0; i < OPTION; i++) {
                    ll.get(i).setVisibility(View.VISIBLE);

                    result.get(i).setText(resultList.get(position).getList().get(i) + "票");
                    textContent.get(i).setText(voteList.get(position).getOptions().get(i));
                }
            }
        });
    }
}
