package com.example.wyf.classchat.adapter;

import android.content.Context;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.bean.VoteBean;
import com.example.wyf.classchat.bean.VoteResult;
import com.example.wyf.classchat.feature.group.vote.VoteActivity;
import com.example.wyf.classchat.feature.group.vote.VoteResult2Activity;
import com.example.wyf.classchat.feature.group.vote.VoteResultActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by gsh on 2017/10/12.
 */

public class VoteAdapter extends RecyclerView.Adapter<VoteAdapter.ViewHolder> {
    private ArrayList<String> adminList;
    private Context context;
    private List<VoteBean> datas;
    private int OPTION;
    private List<VoteResult> result;
    private ArrayList<Integer> multipleCheck;
    private String groupId;
    private String userId;
    private String voteUser;
    private int singleCheckOption;
    private String resultObjectId;
    private String voteObjectId;
    private int myOption;
    private int layoutPosition;

    public VoteAdapter(Context context, List<VoteBean> datas, List<VoteResult> result, String groupId, String userId, ArrayList<String> adminList) {
        this.context = context;
        this.datas = datas;
        this.result = result;
        this.groupId = groupId;
        this.userId = userId;
        this.adminList = adminList;
        Log.i("data", datas.size() + "");
    }


    @Override
    public VoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(context, R.layout.vote_item, null);
        VoteAdapter.ViewHolder holder = new ViewHolder(view);

        return holder;
    }


    public void onBindViewHolder(final VoteAdapter.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final boolean isSingle = datas.get(position).isSingle();
        final List<Integer> list = new ArrayList<>();

        myOption = holder.getLayoutPosition();
        holder.vote_del.setEnabled(true);
        holder.userId.setText(datas.get(myOption).getUserId());
        String vote_state = datas.get(myOption).getState();
        voteObjectId = datas.get(position).getObjectId();
        if (vote_state.equals("doing")) {
            holder.vote_state.setText("进行中");

        } else {
            holder.vote_state.setText("已经完成");
            Toast.makeText(context, "完成" + position, Toast.LENGTH_SHORT).show();
        }

        holder.creatTime.setText(datas.get(myOption).getCreatTime());
        holder.vote_title.setText(datas.get(myOption).getTitle());
        holder.vote_Content.setText(datas.get(myOption).getVoteContent());


        OPTION = datas.get(myOption).getOPTION();
        ;

        // voteObjectId=datas.get(position).getObjectId();
        //       Log.i("voteObjectId",voteObjectId);
        //resultObjectId=result.get(position).getObjectId();
        //       Log.i("resultObjectId1",resultObjectId);

        voteUser = datas.get(position).getUserId();


        Log.i("user", userId);
        Log.i("vote", voteUser);
        if (adminList != null) {
            for (int i = 0; i < adminList.size(); i++) {
                if (adminList.get(i).equals(userId)) {
                    holder.vote_del.setVisibility(View.VISIBLE);
                    holder.vote_del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("go to del ", "go to del first");
                            holder.vote_del.setEnabled(false);
                            final int layoutPosition2 = holder.getLayoutPosition();
                            final VoteBean vb = new VoteBean();
                            voteObjectId = datas.get(layoutPosition2).getObjectId();
                            vb.delete(voteObjectId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        delresult(layoutPosition2);
                                        Log.i("go to del ", "go to del sec");
                                    } else {
                                        Log.i("voteadapterdel", e + "");
                                        vb.delete(new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                delresult(layoutPosition2);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }


        Log.i("OPTION", OPTION + "");
        Log.i("Single", isSingle + "");
        if (isSingle == true) {
            holder.singButton1.setText(datas.get(position).getVoteOption1());
            holder.singButton2.setText(datas.get(position).getVoteOption2());
            holder.singButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (singleCheckOption == 1) {
                        singleCheckOption = 0;
                    } else {
                        singleCheckOption = 1;
                    }
                }
            });
            holder.singButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (singleCheckOption == 2) {
                        singleCheckOption = 0;
                    } else {
                        singleCheckOption = 2;
                    }

                }
            });
            holder.singleLinearLayout.setVisibility(View.VISIBLE);
            //选项内容
            ArrayList<String> options = datas.get(position).getOptions();

            ArrayList<RadioButton> singleButton = new ArrayList<>();
            singleButton.add(holder.singButton3);
            singleButton.add(holder.singButton4);
            singleButton.add(holder.singButton5);
            singleButton.add(holder.singButton6);
            singleButton.add(holder.singButton7);
            singleButton.add(holder.singButton8);
            singleButton.add(holder.singButton9);
            singleButton.add(holder.singButton10);
            singleButton.add(holder.singButton11);
            singleButton.add(holder.singButton12);
            singleButton.add(holder.singButton13);
            singleButton.add(holder.singButton14);
            singleButton.add(holder.singButton15);


            for (int i = 0; i < options.size(); i++) {
                String text = options.get(i);
                singleButton.get(i).setVisibility(View.VISIBLE);
                singleButton.get(i).setText(text);

            }
            for (int i = 0; i < 13; i++) {
                final int finalI = i;
                singleButton.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (singleCheckOption == finalI + 3) {
                            singleCheckOption = 0;
                        } else {
                            singleCheckOption = finalI + 3;
                        }

                    }
                });
            }


        } else {
            holder.multipleButton1.setText(datas.get(position).getVoteOption1());
            holder.multipleButton2.setText(datas.get(position).getVoteOption2());
            holder.multipleLinearLayout.setVisibility(View.VISIBLE);
            ArrayList<String> options = datas.get(position).getOptions();

            final ArrayList<CheckBox> multiple = new ArrayList<>();
            multiple.add(holder.multipleButton3);
            multiple.add(holder.multipleButton4);
            multiple.add(holder.multipleButton5);
            multiple.add(holder.multipleButton6);
            multiple.add(holder.multipleButton7);
            multiple.add(holder.multipleButton8);
            multiple.add(holder.multipleButton9);
            multiple.add(holder.multipleButton10);
            multiple.add(holder.multipleButton11);
            multiple.add(holder.multipleButton12);
            multiple.add(holder.multipleButton13);
            multiple.add(holder.multipleButton14);
            multiple.add(holder.multipleButton15);
            multipleCheck = new ArrayList<>();

            holder.multipleButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (multipleCheck.contains(1)) {
                        for (int i = 0; i < multipleCheck.size(); i++) {
                            if (multipleCheck.get(i).equals(1))
                                multipleCheck.remove(i);
                        }
                    } else {
                        multipleCheck.add(1);
                    }
                }
            });
            holder.multipleButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (multipleCheck.contains(2)) {
                        for (int i = 0; i < multipleCheck.size(); i++) {
                            if (multipleCheck.get(i).equals(2))
                                multipleCheck.remove(i);
                        }
                    } else {
                        multipleCheck.add(2);
                    }
                }
            });
            for (int i = 0; i < 13; i++) {
                final int finalI = i;
                multiple.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (multipleCheck.contains(finalI + 3)) {
                            for (int a = 0; a < multipleCheck.size(); a++) {
                                if (multipleCheck.get(a).equals(finalI + 3))
                                    multipleCheck.remove(a);
                            }
                        } else {
                            multipleCheck.add(finalI + 3);
                        }
                    }
                });
            }


            for (int i = 0; i < options.size(); i++) {
                String text = options.get(i);
                multiple.get(i).setVisibility(View.VISIBLE);
                multiple.get(i).setText(text);

            }
        }
        holder.btn_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("issingle", isSingle + "");
                layoutPosition = holder.getLayoutPosition();
                Log.i("myposition", myOption + "");
                Log.i("layoutPosition", layoutPosition + "");
                if (isSingle) {
                    if (singleCheckOption == 0) {
                        Toast.makeText(context, "请选择一个选项", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("diyg", "dafasf");
                        Intent intent = new Intent(context, VoteResultActivity.class);
                        int position1 = position;
                        intent.putExtra("position", position1);
                        intent.putExtra("singleCheckOption", singleCheckOption);
                        intent.putExtra("groupId", groupId);
                        intent.putExtra("userId", userId);
                        intent.putExtra("voteObjectId", datas.get(layoutPosition).getObjectId());
                        intent.putExtra("resultObjectId", result.get(layoutPosition).getObjectId());
//                    Log.i("resultObjectId",resultObjectId);
                        context.startActivity(intent);
                        singleCheckOption = 0;
                        VoteActivity vote = (VoteActivity) context;
                        vote.finish();
                    }
                } else {
                    if (multipleCheck.size() < 2) {
                        Toast.makeText(context, "请选择2个或者2个以上的选项", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(context, VoteResult2Activity.class);
                        Log.i("dieg", "dasfsa");
                        int position1 = position;
                        intent.putExtra("position", position1);
                        intent.putExtra("multipleCheck", multipleCheck);
                        intent.putExtra("groupId", groupId);
                        intent.putExtra("userId", userId);
                        String putVoteObjectId = datas.get(layoutPosition).getObjectId();
                        String putResultObjectId = result.get(layoutPosition).getObjectId();
                        if (putResultObjectId != null) {
                            intent.putExtra("resultObjectId", result.get(layoutPosition).getObjectId());
                        }
                        if (putVoteObjectId != null) {
                            intent.putExtra("voteObjectId", datas.get(layoutPosition).getObjectId());
                        }

                        context.startActivity(intent);
                        multipleCheck.clear();
                        VoteActivity vote = (VoteActivity) context;
                        vote.finish();
                    }
                }

            }
        });
    }

    private void delresult(int layoutPosition2) {
        final VoteResult vr = new VoteResult();
        Log.i("go to del ", "go to del thir");
        resultObjectId = result.get(layoutPosition2).getObjectId();
        vr.delete(resultObjectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {


                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                    VoteActivity vote = (VoteActivity) context;
                    vote.recreate();

                } else {
                    Log.i("voteadapterdel", e + "");
                    vr.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                        }
                    });
                }
            }
        });
    }

    private void setState(int position) {
        VoteBean vb = new VoteBean();
        vb.setState("done");
        vb.update(datas.get(position).getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("statechagne", "change");
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return datas.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        View voteView;
        private Button btn_vote;
        private TextView userId;
        private TextView vote_Content;
        private TextView vote_state;
        private TextView creatTime;
        private LinearLayout singleLinearLayout;
        private LinearLayout multipleLinearLayout;
        private RadioButton singButton1;
        private RadioButton singButton2;
        private RadioButton singButton3;
        private RadioButton singButton4;
        private RadioButton singButton5;
        private RadioButton singButton6;
        private RadioButton singButton7;
        private RadioButton singButton8;
        private RadioButton singButton9;
        private RadioButton singButton10;
        private RadioButton singButton11;
        private RadioButton singButton12;
        private RadioButton singButton13;
        private RadioButton singButton14;
        private RadioButton singButton15;
        private CheckBox multipleButton1;
        private CheckBox multipleButton2;
        private CheckBox multipleButton3;
        private CheckBox multipleButton4;
        private CheckBox multipleButton5;
        private CheckBox multipleButton6;
        private CheckBox multipleButton7;
        private CheckBox multipleButton8;
        private CheckBox multipleButton9;
        private CheckBox multipleButton10;
        private CheckBox multipleButton11;
        private CheckBox multipleButton12;
        private CheckBox multipleButton13;
        private CheckBox multipleButton14;
        private CheckBox multipleButton15;
        private Button vote_del;
        private TextView vote_title;

        public ViewHolder(View itemView) {
            super(itemView);
            voteView = itemView;
            vote_title = itemView.findViewById(R.id.vote_title);
            creatTime = itemView.findViewById(R.id.start_time);
            btn_vote = itemView.findViewById(R.id.vote_toupiao);
            vote_Content = itemView.findViewById(R.id.vote_content);
            vote_state = itemView.findViewById(R.id.vote_state);

            userId = itemView.findViewById(R.id.vote_userid);
            singleLinearLayout = itemView.findViewById(R.id.vote_single);
            multipleLinearLayout = itemView.findViewById(R.id.vote_multiple);
            singButton1 = itemView.findViewById(R.id.vote_single1);
            singButton2 = itemView.findViewById(R.id.vote_single2);
            singButton3 = itemView.findViewById(R.id.vote_single3);
            singButton4 = itemView.findViewById(R.id.vote_single4);
            singButton5 = itemView.findViewById(R.id.vote_single5);
            singButton6 = itemView.findViewById(R.id.vote_single6);
            singButton7 = itemView.findViewById(R.id.vote_single7);
            singButton8 = itemView.findViewById(R.id.vote_single8);
            singButton9 = itemView.findViewById(R.id.vote_single9);
            singButton10 = itemView.findViewById(R.id.vote_single10);
            singButton11 = itemView.findViewById(R.id.vote_single11);
            singButton12 = itemView.findViewById(R.id.vote_single12);
            singButton13 = itemView.findViewById(R.id.vote_single13);
            singButton14 = itemView.findViewById(R.id.vote_single14);
            singButton15 = itemView.findViewById(R.id.vote_single15);
            multipleButton1 = itemView.findViewById(R.id.vote_multiple1);
            multipleButton2 = itemView.findViewById(R.id.vote_multiple2);
            multipleButton3 = itemView.findViewById(R.id.vote_multiple3);
            multipleButton4 = itemView.findViewById(R.id.vote_multiple4);
            multipleButton5 = itemView.findViewById(R.id.vote_multiple5);
            multipleButton6 = itemView.findViewById(R.id.vote_multiple6);
            multipleButton7 = itemView.findViewById(R.id.vote_multiple7);
            multipleButton8 = itemView.findViewById(R.id.vote_multiple8);
            multipleButton9 = itemView.findViewById(R.id.vote_multiple9);
            multipleButton10 = itemView.findViewById(R.id.vote_multiple10);
            multipleButton11 = itemView.findViewById(R.id.vote_multiple11);
            multipleButton12 = itemView.findViewById(R.id.vote_multiple12);
            multipleButton13 = itemView.findViewById(R.id.vote_multiple13);
            multipleButton14 = itemView.findViewById(R.id.vote_multiple14);
            multipleButton15 = itemView.findViewById(R.id.vote_multiple15);
            vote_del = itemView.findViewById(R.id.btn_votedel);
        }
    }
}
