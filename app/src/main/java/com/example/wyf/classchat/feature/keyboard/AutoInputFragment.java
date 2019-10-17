package com.example.wyf.classchat.feature.keyboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/2/23/023.
 */

public class AutoInputFragment extends BaseFragment {

    @BindView(R.id.viewpager_auto_input)
    ViewPager viewPager;
    @BindView(R.id.recyclerview_auto_input)
    RecyclerView recyclerViewAutoInput;
    @BindView(R.id.ll_expression)
    LinearLayout llExpression;

    private int emotion_map_type = EmotionUtils.EMOTION_CLASSIC_TYPE;
    private EmotionKeyboard.OnClickExpressionBtnListener listener;
    private int prePosition = 0;
    private View contentView;
    private EditText mEditText;
    private View expressionBtn;
    private List<ImageModel> list = new ArrayList<>();
    private AutoInputAdapter adapter;
    private EmotionKeyboard keyboard;

    public static AutoInputFragment newInstance() {
        return new AutoInputFragment();
    }

    @Override
    public void initData() {
        keyboard = EmotionKeyboard.with(getActivity())
                .setEmotionView(llExpression)//绑定表情面板
                .bindToContent(contentView)
                .bindToEditText(mEditText)//判断绑定那种EditView
                .bindToEmotionButton(expressionBtn)//绑定表情按钮
                .setOnClickExpressionBtnListener(listener)
                .build();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                modifyItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initHorizontalRecyclerView();
        initFragments();
    }

    public LinearLayout getEmotionBar() {
        return llExpression;
    }

    public EmotionKeyboard getKeyboard() {
        return keyboard;
    }

    private void initHorizontalRecyclerView() {
        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                list.add(new ImageModel(R.drawable.ic_expression_normal, true));
            } else {
                list.add(new ImageModel(R.drawable.ic_add_friend, false));
            }
        }
        recyclerViewAutoInput.setHasFixedSize(true);
        adapter = new AutoInputAdapter(mActivity, list);
        adapter.setOnClickItemListener((view, curPosition, datas) -> {
            modifyItem(curPosition);
        });
        recyclerViewAutoInput.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewAutoInput.setLayoutManager(linearLayoutManager);
    }

    private void modifyItem(int curPosition) {
        list.get(prePosition).setSelected(false);
        list.get(curPosition).setSelected(true);
        adapter.notifyItemChanged(prePosition);
        adapter.notifyItemChanged(curPosition);
        viewPager.setCurrentItem(curPosition);
        prePosition = curPosition;
    }

    private void initFragments() {
        List<Fragment> fragments = new ArrayList<>();
        EmotionComplateFragment emotionComplateFragment = EmotionComplateFragment.newInstance();
        emotionComplateFragment.setOnEmotionClickListener((parent, view, position, id) -> {
            // 点击的是表情
            EmotionGridViewAdapter emotionGvAdapter = (EmotionGridViewAdapter) parent.getAdapter();
            if (position == emotionGvAdapter.getCount() - 1) {
                // 如果点击了最后一个回退按钮,则调用删除键事件
                mEditText.dispatchKeyEvent(new KeyEvent(
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {
                // 如果点击了表情,则添加到输入框中
                String emotionName = emotionGvAdapter.getItem(position);
                // 获取当前光标位置,在指定位置上添加表情图片文本
                int curPosition = mEditText.getSelectionStart();
                StringBuilder sb = new StringBuilder(mEditText.getText().toString());
                sb.insert(curPosition, emotionName);
                // 特殊文字处理,将表情等转换一下
                mEditText.setText(SpanStringUtils.getEmotionContent(emotion_map_type,
                        mActivity, mEditText, sb.toString()));
                // 将光标设置到新增完表情的右侧
                mEditText.setSelection(curPosition + emotionName.length());
            }
        });
        fragments.add(emotionComplateFragment);
        Bundle b;
        for (int i = 0; i < 5; i++) {
            b = new Bundle();
            b.putString("going", "Fragment-" + i);
            Fragment1 fg = Fragment1.newInstance(b);
            fragments.add(fg);
        }
        AutoInputViewPagerAdapter adapter = new AutoInputViewPagerAdapter(mActivity.getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }

    public AutoInputFragment bindToContentView(View contentView) {
        this.contentView = contentView;
        return this;
    }

    public AutoInputFragment bindToEditText(EditText etInput) {
        this.mEditText = etInput;
        return this;
    }

    public AutoInputFragment bindToExpressionButton(View expressionBtn) {
        this.expressionBtn = expressionBtn;
        return this;
    }

    public AutoInputFragment setOnClickExpressionBtnListener(EmotionKeyboard.OnClickExpressionBtnListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_auto_input;
    }

    public boolean isInterceptBackPress() {
        return keyboard.interceptBackPress();
    }

}
