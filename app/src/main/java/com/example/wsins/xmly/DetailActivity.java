package com.example.wsins.xmly;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wsins.xmly.adapters.DetailListAdapter;
import com.example.wsins.xmly.base.BaseActivity;
import com.example.wsins.xmly.base.BaseApplication;
import com.example.wsins.xmly.interfaces.IAlbumDetailViewCallBack;
import com.example.wsins.xmly.interfaces.IPlayerCallBack;
import com.example.wsins.xmly.interfaces.ISubscriptionCallback;
import com.example.wsins.xmly.presenters.AlbumDetailPresenter;
import com.example.wsins.xmly.presenters.PlayerPresenter;
import com.example.wsins.xmly.presenters.SubscriptionPresenter;
import com.example.wsins.xmly.utils.Constants;
import com.example.wsins.xmly.utils.ImageBlurMaker;
import com.example.wsins.xmly.utils.LogUtil;
import com.example.wsins.xmly.views.RoundRectImageView;
import com.example.wsins.xmly.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallBack, UILoader.OnRetryClickListener, DetailListAdapter.ItemClickListener, IPlayerCallBack, ISubscriptionCallback {

    private static final String TAG = "DetailActivity";
    private ImageView largeCover;
    private RoundRectImageView smallCover;
    private TextView albumTitle;
    private TextView albumAuthor;
    private AlbumDetailPresenter albumDetailPresenter;
    private int currentPage = 1;
    private RecyclerView detailList;
    private DetailListAdapter detailListAdapter;
    private FrameLayout detailListContainer;
    private UILoader uiLoader;
    private long currentId = -1;
    private ImageView playControlBtn;
    private TextView playControlTips;
    private PlayerPresenter playerPresenter;
    private List<Track> currentTracks = null;
    private final static int DEFAULT_PLAY_INDEX = 0;
    private TwinklingRefreshLayout mRefreshLayout;
    private String currentTrackTitle;
    private TextView subBtn;
    private SubscriptionPresenter subscriptionPresenter;
    private Album currentAlbum;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
        initPresenter();
        //设置订阅按钮状态
        updateSubState();
        updatePlayState(playerPresenter.isPlaying());
        initListener();
    }

    private void updateSubState() {
        if (subscriptionPresenter != null) {
            boolean isSubscription = subscriptionPresenter.isSubscription(currentAlbum);
            subBtn.setText(isSubscription ? R.string.cancel_sub_tips_text : R.string.sub_tips_text);
        }
    }

    private void initPresenter() {
        //专辑详情presenter
        albumDetailPresenter = AlbumDetailPresenter.getInstance();
        albumDetailPresenter.registerViewCallBack(this);
        //播放器的presenter
        playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.registerViewCallBack(this);
        //订阅相关的Presenter
        subscriptionPresenter = SubscriptionPresenter.getsInstance();
        subscriptionPresenter.getSubscriptionList();
        subscriptionPresenter.registerViewCallBack(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (albumDetailPresenter != null) {
            albumDetailPresenter.unRegisterViewCallBack(this);
        }
        if (playerPresenter != null) {
            playerPresenter.unRegisterViewCallBack(this);
        }
        if (subscriptionPresenter != null) {
            subscriptionPresenter.unRegisterViewCallBack(this);
        }
    }

    private void initListener() {

        playControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerPresenter != null) {
                    //判断播放器是否有播放列表
                    boolean has = playerPresenter.hasPlayList();
                    if (has) {
                        //去控制播放器状态
                        handlePlayControl();
                    } else {
                        handleNoPlayList();
                    }
                }
            }
        });
        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subscriptionPresenter != null) {
                    boolean isSubscription = subscriptionPresenter.isSubscription(currentAlbum);
                    //如果没有订阅，就去订阅；如果已经订阅了，就取消
                    if (isSubscription) {
                        subscriptionPresenter.deleteSubscription(currentAlbum);
                    } else {
                        subscriptionPresenter.addSubscription(currentAlbum);
                    }
                }
            }
        });

    }

    /**
     * 当播放器里面没有播放内容，我们要进行处理
     */
    private void handleNoPlayList() {
        playerPresenter.setPlayList(currentTracks, DEFAULT_PLAY_INDEX);

    }

    private void handlePlayControl() {
        if (playerPresenter.isPlaying()) {
            //正在播放就暂停
            playerPresenter.pause();
        } else {
            playerPresenter.play();
        }
    }

    private void initView() {
        detailListContainer = this.findViewById(R.id.datail_list_container);
        //
        if (uiLoader == null) {
            uiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            detailListContainer.removeAllViews();
            detailListContainer.addView(uiLoader);
            uiLoader.setOnRetryClickListener(DetailActivity.this);
        }

        largeCover = this.findViewById(R.id.iv_large_cover);
        smallCover = this.findViewById(R.id.viv_small_cover);
        albumTitle = this.findViewById(R.id.tv_album_title);
        albumAuthor = this.findViewById(R.id.tv_album_author);

        //播放控制图标
        playControlBtn = this.findViewById(R.id.detail_play_control);
        playControlTips = this.findViewById(R.id.play_control_tv);
        playControlTips.setSelected(true);

        //
        subBtn = this.findViewById(R.id.detail_sub_btn);

    }

    private boolean isloaderMore = false;

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        detailList = detailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = detailListView.findViewById(R.id.refresh_layout);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        detailList.setLayoutManager(linearLayoutManager);
        //设置适配器
        detailListAdapter = new DetailListAdapter();
        detailList.setAdapter(detailListAdapter);
        //设置item间距
        detailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 2);
                outRect.right = UIUtil.dip2px(view.getContext(), 2);
            }
        });
        detailListAdapter.setItemClickListener(this);
        BezierLayout headerView = new BezierLayout(this);
        mRefreshLayout.setHeaderView(headerView);
        mRefreshLayout.setMaxHeadHeight(140);
        mRefreshLayout.setOverScrollBottomShow(false);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this, "刷新成功...", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishRefreshing();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                //去加载更多内容
                if (albumDetailPresenter != null) {
                    albumDetailPresenter.loadMore();
                    isloaderMore = true;
                }
            }
        });
        return detailListView;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (isloaderMore && mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
            isloaderMore = false;
        }

        this.currentTracks = tracks;
        //判断数据结果，根据结果显示UI
        if (tracks == null && tracks.size() == 0) {
            if (uiLoader != null) {
                uiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }
        if (uiLoader != null) {
            uiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //更新设置UI数据
        detailListAdapter.setData(tracks);
    }

    @Override
    public void onNetWorkError(int errorCode, String errorMsg) {
        //请求发生错误，显示网络异常状态
        if (uiLoader != null) {
            uiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    @Override
    public void onAlbumLoaded(Album album) {
        this.currentAlbum = album;
        long id = album.getId();
        currentId = id;

        //获取专辑详情内容
        if (albumDetailPresenter != null) {
            albumDetailPresenter.getAlbumDatail((int) id, currentPage);
        }


        if (uiLoader != null) {
            uiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }

        if (albumTitle != null) {
            albumTitle.setText(album.getAlbumTitle());
        }
        if (albumAuthor != null) {
            albumAuthor.setText(album.getAnnouncer().getNickname());
        }
        //做毛玻璃效果
        if (largeCover != null && null != largeCover) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(largeCover, new Callback() {
                @Override
                public void onSuccess() {
                    //走到这步才说明时有图片的，开始做毛玻璃
                    ImageBlurMaker.makeBlur(largeCover, DetailActivity.this);
                }

                @Override
                public void onError() {
                    LogUtil.d(TAG, "onError");
                }
            });
        }
        if (smallCover != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(smallCover);
        }
    }

    @Override
    public void onLoaderMoreFinashed(int size) {
        if (size > 0) {
            Toast.makeText(this, "成功加载" + size + "条节目", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有更多节目", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRefreshFinish(boolean isOkay) {

    }

    @Override
    public void onRetryClick() {
        //表示用户网络不佳 的时候去点击了重新加载
        if (albumDetailPresenter != null) {
            albumDetailPresenter.getAlbumDatail((int) currentId, currentPage);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int i) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(detailData, i);
        //跳转到播放器界面
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    /**
     * 根据播放状态修改图标和文字
     *
     * @param playing
     */
    private void updatePlayState(boolean playing) {
        if (playControlBtn != null && playControlTips != null) {
            playControlBtn.setImageResource(playing ?
                    R.drawable.selector_play_control_pause : R.drawable.selector_play_control_play);
            if (!playing) {
                playControlTips.setText(R.string.click_play_tips_text);
            } else {
                if (!currentTrackTitle.isEmpty()) {
                    playControlTips.setText(currentTrackTitle);
                }
            }
        }
    }

    @Override
    public void onPlayStart() {
        //修改图标为暂停状态，文字修改为正在播放
        updatePlayState(true);
    }

    @Override
    public void onPlayPause() {
        //修改图标为正在播放，文字修改为已暂停
        updatePlayState(false);
    }

    @Override
    public void onPlayStop() {
        //修改图标为正在播放，文字修改为已暂停
        updatePlayState(false);
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
            currentTrackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(currentTrackTitle) && playControlTips != null) {
                playControlTips.setText(currentTrackTitle);
            }
        }

    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    public void onAddResult(boolean isSuccess) {
        if (isSuccess) {
            //如果成功了，那就修改UI
            subBtn.setText(R.string.cancel_sub_tips_text);
        }
        //给个Toast
        String tipsText = isSuccess ? "订阅成功" : "订阅失败";
        Toast.makeText(this, tipsText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        if (isSuccess) {
            //如果成功了，那就修改UI
            subBtn.setText(R.string.sub_tips_text);
        }
        //给个Toast
        String tipsText = isSuccess ? "删除成功" : "删除失败";
        Toast.makeText(this, tipsText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albums) {
        //在这个界面不需要处理
        for (Album album : albums) {
            LogUtil.d(TAG, "album -- > " + album.getAlbumTitle());
        }
    }

    @Override
    public void onSubFull() {
        //弹出Toast提示
        Toast.makeText(this, "订阅数量不得超过" + Constants.MAX_SUB_COUNT, Toast.LENGTH_SHORT).show();
    }
}
