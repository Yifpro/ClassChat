package com.example.wyf.classchat.feature.group.vote;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.util.HyphenateUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;

/**
 * Created by gsh on 2017/11/3.
 */

public class VoteChoosePreson extends AppCompatActivity implements View.OnClickListener, IAppInitContract.IActivity,
        IAppInitContract.ISubscribe {
    @BindView(R.id.vote_cy)
    RecyclerView recycle;
    @BindView(R.id.vote_chose_sure)
    Button btn;
    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> checkName = new ArrayList<>();
    private ArrayList<Integer> checkNumber = new ArrayList<>();

    @Override
    public void init() {
        btn.setOnClickListener(this);

        String groupId = getIntent().getStringExtra("groupId");
        Log.e("test data", "init: i go");

        getNumber(groupId);
    }

    @Override
    public int getLayoutId() {
        return R.layout.vote_chose_activyt;
    }

    private void getNumber(String groupId) {
        if (groupId != null) {
            Log.e("test data", "getId: " + groupId);
            HyphenateUtils.getMembers(groupId);
        } else {
            Log.e("test data", "getId: 111");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vote_chose_sure:
                for (int i = 0; i < checkNumber.size(); i++) {
                    String s = nameList.get(checkNumber.get(i));
                    checkName.add(s);
                }
                if (checkName.size() < 2) {
                    Toast.makeText(this, "不可选择少于2个对象", Toast.LENGTH_SHORT).show();
                } else if (checkName.size() > 15) {
                    Toast.makeText(this, "不可选择多与15个对象", Toast.LENGTH_SHORT).show();
                    Log.i("checkName", checkName + "");
                } else {
                    rtData();

                }
                break;
        }
    }

    private void rtData() {
        Intent intent = new Intent();

        HashSet hSet = new HashSet(checkName);
        checkName.clear();
        checkName.addAll(hSet);
        intent.putStringArrayListExtra("checkName", checkName);


        setResult(998, intent);
        finish();
    }

    static class VoteChooseAdapter extends RecyclerView.Adapter<VoteChooseAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<String> data;

        public VoteChooseAdapter(Context mContext, ArrayList<String> nameList) {
            this.mContext = mContext;
            this.data = nameList;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mContext, R.layout.vote_chose_item, null);

            ViewHolder holder = new ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.rb_number.setText(data.get(position));

            final int layoutPosition = holder.getLayoutPosition();

           /* holder.rb_number.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                    *//*  checkName.add(data.get(layoutPosition));*//*


              }
          });*/


        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private CheckBox rb_number;
            private View view;

            public ViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                rb_number = itemView.findViewById(R.id.votecy_rb);
                rb_number.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (myListner != null) {
                    myListner.getItem(getLayoutPosition());
                }
            }
        }

        public static interface mListener {
            void getItem(int position);
        }

        private mListener myListner = null;

        public void setMyListner(mListener myListner) {
            this.myListner = myListner;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if (event.getWhat() == Constants.GET_MEMBERS_INFO) {

            List<PersonBmob> memberList = (List<PersonBmob>) event.getMessage();
            for (int i = 0; i < memberList.size(); i++) {
                String name = memberList.get(i).getName();
                nameList.add(name);
            }
            VoteChooseAdapter adapter = new VoteChooseAdapter(VoteChoosePreson.this, nameList);
            adapter.setMyListner(new VoteChooseAdapter.mListener() {
                @Override
                public void getItem(int position) {
                    Boolean isAdd = true;
                    if (checkNumber.size() > 0) {
                        for (int i = 0; i < checkNumber.size(); i++) {
                            if (checkNumber.get(i).equals(position)) {
                                checkNumber.remove(i);
                                isAdd = false;
                                Log.i("vote remove", "remove" + position);

                            }
                        }
                        if (isAdd) {
                            checkNumber.add(position);
                        }

                        Log.i("vote1 add", "add" + position);
                    } else {
                        checkNumber.add(position);
                        Log.i("vote2 add", "add" + position);
                    }


                }
            });
            int spacingInPixels = 8;
            recycle.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
            recycle.setAdapter(adapter);
            recycle.setLayoutManager(new LinearLayoutManager(VoteChoosePreson.this));

            Log.i("votepersonlistname", memberList.get(0).getName());
        }

    }


    class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }
}
