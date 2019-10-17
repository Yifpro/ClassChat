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

public class VoteResult2Activity extends AppCompatActivity implements IAppInitContract.IActivity {
    @BindView(R.id.voteresult_title)
    TextView title;
    @BindView(R.id.voteresult_content)
    TextView result_content;
    @BindView(R.id.vote_content1)
    TextView voteContent1;
    @BindView(R.id.vote_result1)
    TextView voteResult1;
    @BindView(R.id.ll_1)
    LinearLayout ll1;
    @BindView(R.id.vote_content2)
    TextView voteContent2;
    @BindView(R.id.vote_result2)
    TextView voteResult2;
    @BindView(R.id.ll_2)
    LinearLayout ll2;
    @BindView(R.id.vote_content3)
    TextView voteContent3;
    @BindView(R.id.vote_result3)
    TextView voteResult3;
    @BindView(R.id.ll_3)
    LinearLayout ll3;
    @BindView(R.id.vote_content4)
    TextView voteContent4;
    @BindView(R.id.vote_result4)
    TextView voteResult4;
    @BindView(R.id.ll_4)
    LinearLayout ll4;
    @BindView(R.id.vote_content5)
    TextView voteContent5;
    @BindView(R.id.vote_result5)
    TextView voteResult5;
    @BindView(R.id.ll_5)
    LinearLayout ll5;
    @BindView(R.id.vote_content6)
    TextView voteContent6;
    @BindView(R.id.vote_result6)
    TextView voteResult6;
    @BindView(R.id.ll_6)
    LinearLayout ll6;
    @BindView(R.id.vote_content7)
    TextView voteContent7;
    @BindView(R.id.vote_result7)
    TextView voteResult7;
    @BindView(R.id.ll_7)
    LinearLayout ll7;
    @BindView(R.id.vote_content8)
    TextView voteContent8;
    @BindView(R.id.vote_result8)
    TextView voteResult8;
    @BindView(R.id.ll_8)
    LinearLayout ll8;
    @BindView(R.id.vote_content9)
    TextView voteContent9;
    @BindView(R.id.vote_result9)
    TextView voteResult9;
    @BindView(R.id.ll_9)
    LinearLayout ll9;
    @BindView(R.id.vote_content10)
    TextView voteContent10;
    @BindView(R.id.vote_result10)
    TextView voteResult10;
    @BindView(R.id.ll_10)
    LinearLayout ll10;
    @BindView(R.id.vote_content11)
    TextView voteContent11;
    @BindView(R.id.vote_result11)
    TextView voteResult11;
    @BindView(R.id.ll_11)
    LinearLayout ll11;
    @BindView(R.id.vote_content12)
    TextView voteContent12;
    @BindView(R.id.vote_result12)
    TextView voteResult12;
    @BindView(R.id.ll_12)
    LinearLayout ll12;
    @BindView(R.id.vote_content13)
    TextView voteContent13;
    @BindView(R.id.vote_result13)
    TextView voteResult13;
    @BindView(R.id.ll_13)
    LinearLayout ll13;
    @BindView(R.id.vote_content14)
    TextView voteContent14;
    @BindView(R.id.vote_result14)
    TextView voteResult14;
    @BindView(R.id.ll_14)
    LinearLayout ll14;
    @BindView(R.id.vote_content15)
    TextView voteContent15;
    @BindView(R.id.vote_result15)
    TextView voteResult15;
    @BindView(R.id.ll_15)
    LinearLayout ll15;
    private int votereuslt1;
    private int votereuslt2;
    private String voteOption1;
    private String voteOption2;
    private List<VoteResult> resultList;
    private List<VoteBean> voteList;
    private String state;
    private int OPTION;
    private int position;
    private int getOption1;
    private int getOption2;
    private ArrayList<TextView> textContent = new ArrayList<>();
    private ArrayList<TextView> result = new ArrayList<>();
    private ArrayList<LinearLayout> ll = new ArrayList<>();
    private String voteObjectId;
    private String resultObjectId;
    private ArrayList<String> doneUser;
    private String userId;
    private ArrayList<Integer> multipleCheck;
    private String vote_content;
    private String vote_title;

    private String groupId;
    private Boolean haveDone = false;
    private ArrayList<Integer> list1;
    private ArrayList<String> options;

    @Override
    public int getLayoutId() {
        return R.layout.vote_resultactivity;
    }

    @Override
    public void init() {
        textContent.add(voteContent3);
        textContent.add(voteContent4);
        textContent.add(voteContent5);
        textContent.add(voteContent6);
        textContent.add(voteContent7);
        textContent.add(voteContent8);
        textContent.add(voteContent9);
        textContent.add(voteContent10);
        textContent.add(voteContent11);
        textContent.add(voteContent12);
        textContent.add(voteContent13);
        textContent.add(voteContent14);
        textContent.add(voteContent15);

        result.add(voteResult3);
        result.add(voteResult4);
        result.add(voteResult5);
        result.add(voteResult6);
        result.add(voteResult7);
        result.add(voteResult8);
        result.add(voteResult9);
        result.add(voteResult10);
        result.add(voteResult11);
        result.add(voteResult12);
        result.add(voteResult13);
        result.add(voteResult14);
        result.add(voteResult15);

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
        VoteResult VoteResult = new VoteResult();
//注意：不能调用gameScore.setObjectId("")方法

        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < doneUser.size(); i++) {
            arrayList.add(doneUser.get(i));
        }
        arrayList.add(userId);
        VoteResult.setDoneUser(arrayList);


        Log.i("multipleCheck", "" + multipleCheck);
        if (multipleCheck.contains(1)) {
            Toast.makeText(this, "diyg", Toast.LENGTH_SHORT).show();
            getOption1 = votereuslt1 + 1;
            Log.i("getOption1", votereuslt1 + "");
            VoteResult.setOption1(getOption1);
            VoteResult.setOption2(votereuslt2);
            VoteResult.setList(list1);
            for (int i = 0; i < multipleCheck.size(); i++) {
                if (multipleCheck.get(i).equals(1))
                    multipleCheck.remove(i);
            }
            Log.i("multipleCheck2", "" + multipleCheck);
            if (multipleCheck.contains(2)) {
                getOption2 = votereuslt2 + 1;
                VoteResult.setOption2(getOption2);
                VoteResult.setOption1(getOption1);
                VoteResult.setList(list1);
                Log.i("getOption2", "" + getOption2);
                for (int i = 0; i < multipleCheck.size(); i++) {
                    if (multipleCheck.get(i).equals(2))
                        multipleCheck.remove(i);
                }
                for (int i = 0; i < multipleCheck.size(); i++) {

                    int a = list1.get(multipleCheck.get(i) - 3) + 1;
                    list1.remove(multipleCheck.get(i) - 3);
                    list1.add(multipleCheck.get(i) - 3, a);
                    VoteResult.setOption2(getOption2);
                    VoteResult.setOption1(getOption1);
                    VoteResult.setList(resultList.get(position).getList());
                }
            } else {
                if (multipleCheck.size() > 0) {
                    for (int i = 0; i < multipleCheck.size(); i++) {

                        int a = list1.get(multipleCheck.get(i) - 3) + 1;
                        list1.remove(multipleCheck.get(i) - 3);
                        list1.add(multipleCheck.get(i) - 3, a);
                        VoteResult.setOption2(votereuslt2);
                        VoteResult.setOption1(getOption1);
                        VoteResult.setList(list1);
                    }

                }
            }
        }
        //dianledierge
        else if (multipleCheck.contains(2)) {
            Toast.makeText(this, "dierge", Toast.LENGTH_SHORT).show();
            getOption2 = resultList.get(position).getOption2() + 1;
            VoteResult.setOption2(getOption2);
            VoteResult.setList(list1);
            VoteResult.setOption1(votereuslt1);
            for (int i = 0; i < multipleCheck.size(); i++) {
                if (multipleCheck.get(i).equals(2))
                    multipleCheck.remove(i);
            }
            Log.i("getOption2", getOption2 + "");
            Log.i("multipleCheck", multipleCheck + "");
            if (multipleCheck.size() > 0) {
                for (int i = 0; i < multipleCheck.size(); i++) {

                    int a = list1.get(multipleCheck.get(i) - 3) + 1;
                    list1.remove(multipleCheck.get(i) - 3);
                    Log.i("multipleCheck.get(i)", multipleCheck.get(i) + "");
                    list1.add(multipleCheck.get(i) - 3, a);
                    Log.i("ic_search", a + "");
                    VoteResult.setList(resultList.get(position).getList());
                    VoteResult.setOption2(getOption2);
                    VoteResult.setOption1(votereuslt1);
                }
            }
        }
        //zhidianledisange
        else {
            Toast.makeText(this, "disange", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < multipleCheck.size(); i++) {

                int a = list1.get(multipleCheck.get(i) - 3) + 1;
                list1.remove(multipleCheck.get(i) - 3);
                list1.add(multipleCheck.get(i) - 3, a);

                VoteResult.setList(list1);
                VoteResult.setOption2(votereuslt2);
                VoteResult.setOption1(votereuslt1);
            }
        }

        VoteResult.update(resultObjectId, new UpdateListener() {


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

    /*    private void uploadDatas() {
            for (int i=0;i<multipleCheck.size();i++){
                VoteResult VoteResult = new VoteResult();
    //注意：不能调用gameScore.setObjectId("")方法

                ArrayList<String> arrayList=new ArrayList<>();
                for (int a=0;a<doneUser.size();a++){
                    arrayList.add(doneUser.get(a));
                }
                arrayList.add(userId);
                VoteResult.setDoneUser(arrayList);
                if (multipleCheck.get(i).equals(1)){

                    getOption1 = votereuslt1 + 1;
                    Log.i("getOption1", votereuslt1 + "");
                    VoteResult.setOption1(getOption1);
                }else if (multipleCheck.get(i).equals(2)){

                    getOption2 = votereuslt2 + 1;
                    Log.i("getOption2", votereuslt2 + "");
                    VoteResult.setOption2(getOption2);
                }else{

                    Log.i("disange","zou le di san");
                    Log.i("multipleCheck"+i,multipleCheck+"");
                    int number=multipleCheck.get(i)-1;
                    int integer = resultList.get(position).getList().get(number)+1;
                    Log.i("integer vote",integer+"");

                    list1.remove(number);
                    list1.add(number,integer);
                    Log.i("number vote",number+"");
                    Log.i("list1 vote",list1+"");

                }
                VoteResult.setList(list1);
                VoteResult.update(resultObjectId, new UpdateListener() {


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

        }*/
    private void setDatas() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title.setText("标题  " + vote_title);

                result_content.setText("内容  " + vote_content);
                voteContent1.setText(voteOption1);
                voteContent2.setText(voteOption2);
                voteResult1.setText((getOption1) + "票");
                voteResult2.setText((getOption2) + "票");
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
        groupId = getIntent().getStringExtra("groupId");
        multipleCheck = getIntent().getIntegerArrayListExtra("multipleCheck");
        userId = getIntent().getStringExtra("userId");
        position = 0;
        Log.i("position", position + "");
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
                querryVoteBean(list);

            }
        });
    }

    private void querryVoteBean(final List<VoteResult> list) {
        BmobQuery<VoteBean> query = new BmobQuery<VoteBean>();
        query.addWhereEqualTo("objectId", voteObjectId);
//执行查询方法
        query.findObjects(new FindListener<VoteBean>() {
            @Override
            public void done(List<VoteBean> object, BmobException e) {
                if (e == null) {

                    resultList = list;

                    doneUser = resultList.get(position).getDoneUser();
                    votereuslt1 = resultList.get(position).getOption1();
                    list1 = resultList.get(position).getList();
                    Log.i("votereuslt1", votereuslt1 + "");
                    votereuslt2 = resultList.get(position).getOption2();
                    Log.i("votereuslt2", votereuslt2 + "");
                    getOption1 = votereuslt1;
                    getOption2 = votereuslt2;
                    voteList = object;
                    voteList.get(position).getOptions();
                    state = voteList.get(position).getState();
                    OPTION = voteList.get(position).getOPTION();
                    voteOption1 = voteList.get(position).getVoteOption1();
                    voteOption2 = voteList.get(position).getVoteOption2();
                    vote_content = voteList.get(position).getVoteContent();
                    vote_title = voteList.get(position).getTitle();
                    Log.i("voteOption2", voteOption2);
                    options = voteList.get(position).getOptions();
                    if (list1 != null) {
                        ifDone();
                    } else {
                        Toast.makeText(VoteResult2Activity.this, "网络不顺畅请稍后继续", Toast.LENGTH_SHORT).show();
                    }
                    /*showDatas();*/
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
            Toast.makeText(VoteResult2Activity.this, "你已经投过票了", Toast.LENGTH_SHORT).show();

            showDatas();
        } else {
            uploadDatas();
        }

    }

    private void showDatas() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title.setText("标题  " + vote_title);

                result_content.setText("内容  " + vote_content);
                voteContent1.setText(voteOption1);
                voteContent2.setText(voteOption2);
                Log.i("votereuslt1", votereuslt1 + "");
                Log.i("votereuslt2", votereuslt2 + "");
                voteResult1.setText(votereuslt1 + "票");
                voteResult2.setText(votereuslt2 + "票");
                Log.i("option", OPTION + "");
                Log.i("list1", list1 + "");
                for (int i = 0; i < OPTION; i++) {
                    ll.get(i).setVisibility(View.VISIBLE);

                    result.get(i).setText(resultList.get(position).getList().get(i) + "票");
                    textContent.get(i).setText(voteList.get(position).getOptions().get(i));
                }
            }
        });
    }
}
