package com.example.wsins.xmly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wsins.xmly.adapters.IndicatorAdapter;
import com.example.wsins.xmly.adapters.MainContentAdapter;
import com.example.wsins.xmly.interfaces.IPlayerCallBack;
import com.example.wsins.xmly.presenters.PlayerPresenter;
import com.example.wsins.xmly.presenters.RecommendPersenter;
import com.example.wsins.xmly.utils.LogUtil;
import com.example.wsins.xmly.views.RoundRectImageView;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

public class MainActivity extends FragmentActivity implements IPlayerCallBack {

    private static final String TAG = "MainActivity";
    private MagicIndicator magicIndicator;
    private ViewPager contentPager;

    private IndicatorAdapter indicatorAdapter;

    private RoundRectImageView trackCover;
    private TextView headTitle;
    private TextView subTitle;
    private ImageView playControl;
    private View playControlItem;
    private PlayerPresenter playerPresenter;
    private View searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.registerViewCallBack(this);

    }

    private void initEvent() {
        indicatorAdapter.setOnIndicatorTabClickListener(new IndicatorAdapter.OnIndicatorTabClickListener() {
            @Override
            public void onTabClick(int i) {
                LogUtil.d(TAG, "click index is --> " + i);
                if (contentPager != null) {
                    contentPager.setCurrentItem(i, false);
                }
            }
        });
        playControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerPresenter != null) {
                    boolean hasPlayList = playerPresenter.hasPlayList();
                    if (!hasPlayList) {
                        //没有设置过播放列表，我们就播放默认的第一个推荐专辑
                        //第一个推荐专辑每天都会变
                        playFirstRecommend();
                    } else {
                        if (playerPresenter.isPlaying()) {
                            playerPresenter.pause();
                        } else {
                            playerPresenter.play();
                        }
                    }
                }
            }
        });
        playControlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPlayList = playerPresenter.hasPlayList();
                if (!hasPlayList) {
                    playFirstRecommend();
                }
                //跳转到播放器界面
                startActivity(new Intent(MainActivity.this, PlayerActivity.class));
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

    }

    /**
     * 播放第一个专辑内容
     */
    private void playFirstRecommend() {
        List<Album> currentRecommend = RecommendPersenter.getsInstance().getCurrentRecommend();
        if (currentRecommend != null && currentRecommend.size() > 0) {
            Album album = currentRecommend.get(0);
            long albumId = album.getId();
            playerPresenter.playByAlbumId(albumId);
        }
    }

    private void initView() {
        magicIndicator = this.findViewById(R.id.main_indicator);
        magicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建indicator的适配器
        indicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(indicatorAdapter);

        //ViewPager
        contentPager = this.findViewById(R.id.content_pager);

        //创建内容适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);
        contentPager.setAdapter(mainContentAdapter);
        //把ViewPager和indicator绑定到一起
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, contentPager);

        //播放控制相关
        trackCover = this.findViewById(R.id.main_track_cover);
        headTitle = this.findViewById(R.id.main_head_title);
        headTitle.setSelected(true);
        subTitle = this.findViewById(R.id.main_sub_title);
        playControl = this.findViewById(R.id.main_play_control);
        playControlItem = findViewById(R.id.main_play_control_item);

        //搜索
        searchBtn = this.findViewById(R.id.search_btn);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerPresenter != null) {
            playerPresenter.unRegisterViewCallBack(this);
        }
    }

    @Override
    public void onPlayStart() {
        updatePlayControl(true);

    }

    private void updatePlayControl(boolean isPlaying) {
        if (playControl != null) {
            playControl.setImageResource(isPlaying ? R.drawable.selector_player_pause : R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayPause() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayError(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentIndex, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();

            LogUtil.d(TAG, "trackTitle --> " + trackTitle);
            if (headTitle != null) {
                headTitle.setText(trackTitle);
            }
            LogUtil.d(TAG, "nickname --> " + nickname);
            if (subTitle != null) {
                subTitle.setText(nickname);
            }
            LogUtil.d(TAG, "coverUrlMiddle --> " + coverUrlMiddle);
            Picasso.with(this).load(coverUrlMiddle).into(trackCover);
        }

    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
