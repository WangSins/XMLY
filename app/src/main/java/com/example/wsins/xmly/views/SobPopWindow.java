package com.example.wsins.xmly.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.wsins.xmly.R;
import com.example.wsins.xmly.adapters.PlayListAdapter;
import com.example.wsins.xmly.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public class SobPopWindow extends PopupWindow {

    private final View popView;
    private LinearLayout playListControlContainer;
    private RecyclerView tracksList;
    private TextView closeBtn;
    private PlayListAdapter playListAdapter;
    private TextView playModeTv;
    private ImageView playModeIv;
    private View playPlayModeContainer;
    private PlaylistPlayModeClickListener mPlayModeClickListener = null;

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

        playModeTv = popView.findViewById(R.id.play_list_play_mode_tv);
        playModeIv = popView.findViewById(R.id.play_list_play_mode_iv);
        playPlayModeContainer = popView.findViewById(R.id.play_list_play_mode_container);


    }

    private void initEvent() {
        //点击关闭以后，窗口消失
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobPopWindow.this.dismiss();
            }
        });
        playPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放模式
                if (mPlayModeClickListener != null) {
                    mPlayModeClickListener.onPlayModeClick();
                }

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
            tracksList.scrollToPosition(position);
        }
    }

    public void setPlayListItemClickListener(PlayListItemClickListener listener) {
        playListAdapter.setOnItemClickListener(listener);
    }

    /**
     * 更新播放列表的播放模式
     *
     * @param currentMode
     */
    public void updatePlayMode(XmPlayListControl.PlayMode currentMode) {
        updatePlayModeImg(currentMode);
    }

    /**
     * 根据当前状态更新播放图标
     */
    private void updatePlayModeImg(XmPlayListControl.PlayMode playMode) {
        int resId = R.drawable.selector_play_mode_list_order;
        int textId = R.string.play_mode_order_text;
        switch (playMode) {
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_play_mode_list_order;
                textId = R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_play_mode_random;
                textId = R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_play_mode_list_loop;
                textId = R.string.play_mode_list_play_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_play_mode_single_loop;
                textId = R.string.play_mode_single_play_text;
                break;
        }
        playModeIv.setImageResource(resId);
        playModeTv.setText(textId);

    }

    public interface PlayListItemClickListener {
        void onItemClick(int positon);
    }

    public void setPlaylistPlayModeClickListener(PlaylistPlayModeClickListener playModeClickListener) {
        mPlayModeClickListener = playModeClickListener;
    }

    public interface PlaylistPlayModeClickListener {
        void onPlayModeClick();
    }

}
