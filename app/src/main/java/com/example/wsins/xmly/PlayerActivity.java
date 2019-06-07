package com.example.wsins.xmly;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.wsins.xmly.adapters.PalyerTrackPageAdapter;
import com.example.wsins.xmly.base.BaseActivity;
import com.example.wsins.xmly.interfaces.IPlayerCallBack;
import com.example.wsins.xmly.presenters.PlayerPresenter;
import com.example.wsins.xmly.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.*;

public class PlayerActivity extends BaseActivity implements IPlayerCallBack, ViewPager.OnPageChangeListener {

    private static final String TAG = "PlayerActivity";
    private ImageView conttrolBtn;
    private PlayerPresenter playerPresenter;
    private SimpleDateFormat minFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView totalDurationTv;
    private TextView currentPositonTV;
    private SeekBar durationBar;
    private int currentProgress = 0;
    private boolean isUserTouchProgress = false;
    private ImageView playPreIv;
    private ImageView playNextIv;
    private TextView trackTitleTv;
    private String trackTitleText;
    private ViewPager trackPageView;
    private PalyerTrackPageAdapter palyerTrackPageAdapter;
    private boolean isUserSlidePage = false;
    private ImageView playerModeSwitch;
    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> playModeRule = new HashMap<>();
    private XmPlayListControl.PlayMode currentMode = PLAY_MODEL_LIST;

    //处理播放模式的切换
    //列表播放：PLAY_MODEL_LIST (默认)
    //列表循环：PLAY_MODEL_LIST_LOOP
    //随机播放：PLAY_MODEL_RANDOM
    //单曲循环: PLAY_MODEL_SINGLE_LOOP
    static {
        playModeRule.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        playModeRule.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM);
        playModeRule.put(PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP);
        playModeRule.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.registerViewCallBack(this);
        //在界面初始化以后才去获取数据
        playerPresenter.getPlayList();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (playerPresenter != null) {
            playerPresenter.unRegisterViewCallBack(this);
            playerPresenter = null;
        }
    }

    /**
     * 找到各个控件
     */
    private void initView() {

        trackTitleTv = this.findViewById(R.id.track_title);
        if (!TextUtils.isEmpty(trackTitleText)) {
            trackTitleTv.setText(trackTitleText);
        }

        trackPageView = this.findViewById(R.id.track_page_view);
        //创建适配器
        palyerTrackPageAdapter = new PalyerTrackPageAdapter();
        //设置适配器
        trackPageView.setAdapter(palyerTrackPageAdapter);

        //切换播放模式
        playerModeSwitch = this.findViewById(R.id.player_mode_switch);

        conttrolBtn = this.findViewById(R.id.play_or_pause);
        totalDurationTv = this.findViewById(R.id.track_duration);
        currentPositonTV = this.findViewById(R.id.current_positon);
        durationBar = this.findViewById(R.id.track_seek_bar);
        playPreIv = this.findViewById(R.id.play_pre);
        playNextIv = this.findViewById(R.id.play_next);


    }

    /**
     * 给控件设置相关的事件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        conttrolBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果现在状态正在播放就暂停
                if (playerPresenter.isPlay()) {
                    playerPresenter.pause();
                } else {
                    //非播放就播放
                    playerPresenter.play();
                }
            }
        });

        durationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if (isFromUser) {
                    currentProgress = progress;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserTouchProgress = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //手离开拖动进度条更新
                isUserTouchProgress = false;
                playerPresenter.seekTo(currentProgress);

            }
        });

        playPreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放前一个节目
                if (playerPresenter != null) {
                    playerPresenter.playPre();
                }

            }
        });
        playNextIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放下一个节目
                if (playerPresenter != null) {
                    playerPresenter.playNext();
                }

            }
        });

        trackPageView.addOnPageChangeListener(this);
        trackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        isUserSlidePage = true;
                        break;
                }
                return false;
            }
        });

        playerModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据当前的mode获取下一个mode
                XmPlayListControl.PlayMode playMode = playModeRule.get(currentMode);
                //修改播放模式
                if (playerPresenter != null) {
                    playerPresenter.switchPlayMode(playMode);
                }
            }
        });

    }

    /**
     * 根据当前状态更新播放图标
     */
    private void updatePlayModeImg() {
        int resId = R.drawable.selector_play_mode_list_order;
        switch (currentMode) {
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_play_mode_list_order;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_play_mode_pandom;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_play_mode_list_loop;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_play_mode_single_loop;
                break;
        }
        playerModeSwitch.setImageResource(resId);

    }

    @Override
    public void onPlayStart() {
        //开始播放，修改UI层暂停
        if (conttrolBtn != null) {
            conttrolBtn.setImageResource(R.drawable.selector_player_stop);
        }
    }

    @Override
    public void onPlayPause() {
        if (conttrolBtn != null) {
            conttrolBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayStop() {
        if (conttrolBtn != null) {
            conttrolBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayError(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {
        LogUtil.d(TAG, "list -> " + list);
        //把数据设置到适配器里
        if (palyerTrackPageAdapter != null) {
            palyerTrackPageAdapter.setData(list);
        }

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        //更新播放模式，并且修改UI
        currentMode = playMode;
        updatePlayModeImg();

    }

    @Override
    public void onProgressChange(int currentDuration, int total) {
        durationBar.setMax(total);
        //更新进度条
        String totalDuration;
        String currentPosition;
        if (total > 1000 * 60 * 60) {
            totalDuration = hourFormat.format(total);
            currentPosition = hourFormat.format(currentDuration);
        } else {
            totalDuration = minFormat.format(total);
            currentPosition = minFormat.format(currentDuration);
        }
        if (totalDurationTv != null) {
            totalDurationTv.setText(totalDuration);
        }
        //更新当前时间
        if (currentPositonTV != null) {
            currentPositonTV.setText(currentPosition);
        }

        //更新进度
        //计算进度
        if (!isUserTouchProgress) {
//            int percent = (int) (currentDuration * 1.0f / total * 100);
//            LogUtil.d(TAG, "percent -->" + percent);
            durationBar.setProgress(currentDuration);
        }

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        this.trackTitleText = track.getTrackTitle();
        if (trackTitleTv != null) {
            //设置当前节目标题
            trackTitleTv.setText(trackTitleText);
        }
        //当节目改变的时候，获取当前播放器位置
        //当前节目改变以后，修改页面图片
        if (trackPageView != null) {
            trackPageView.setCurrentItem(playIndex, true);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        LogUtil.d(TAG, "i -> " + i);
        //当页面选中的时候，切换播放内容
        if (playerPresenter != null && isUserSlidePage) {
            playerPresenter.playByIndex(i);
        }
        isUserSlidePage = false;
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
