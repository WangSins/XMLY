package com.example.wsins.xmly.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.wsins.xmly.R;
import com.example.wsins.xmly.adapters.PlayListAdapter;
import com.example.wsins.xmly.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public class SobPopWindow extends PopupWindow {

    private final View popView;
    private LinearLayout playListControlContainer;
    private RecyclerView tracksList;
    private TextView closeBtn;
    private PlayListAdapter playListAdapter;

    public SobPopWindow() {
        //设置宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置setOutsideTouchable前，需要先设置setBackgroundDrawable，否则点击外部无法关闭
        setOutsideTouchable(true);

        //载入View
        popView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(popView);
        //设置窗口进入和退出动画
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();
    }

    private void initView() {
        playListControlContainer = popView.findViewById(R.id.play_list_control_container);
        //先找到控件
        tracksList = popView.findViewById(R.id.play_list_rv);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        tracksList.setLayoutManager(linearLayoutManager);
        //设置适配器
        playListAdapter = new PlayListAdapter();
        tracksList.setAdapter(playListAdapter);

        closeBtn = popView.findViewById(R.id.play_list_close_btn);

    }

    private void initEvent() {
        //点击关闭以后，窗口消失
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobPopWindow.this.dismiss();
            }
        });
    }

    /**
     * 给适配器设置数据
     *
     * @param data
     */
    public void setListData(List<Track> data) {
        if (playListAdapter != null) {
            playListAdapter.setData(data);
        }

    }

    public void setCurrentPlayPosition(int position) {
        if (playListAdapter != null) {
            playListAdapter.setCurrentPlayPosition(position);
        }
    }

}
