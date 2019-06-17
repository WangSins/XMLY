package com.example.wsins.xmly;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wsins.xmly.adapters.DetailListAdapter;
import com.example.wsins.xmly.base.BaseActivity;
import com.example.wsins.xmly.interfaces.IAlbumDetailViewCallBack;
import com.example.wsins.xmly.interfaces.IPlayerCallBack;
import com.example.wsins.xmly.presenters.AlbumDetailPresenter;
import com.example.wsins.xmly.presenters.PlayerPresenter;
import com.example.wsins.xmly.utils.ImageBlurMaker;
import com.example.wsins.xmly.utils.LogUtil;
import com.example.wsins.xmly.views.RoundRectImageView;
import com.example.wsins.xmly.views.UILoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallBack, UILoader.OnRetryClickListener, DetailListAdapter.ItemClickListener, IPlayerCallBack {

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

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
        //专辑详情presenter
        albumDetailPresenter = AlbumDetailPresenter.getInstance();
        albumDetailPresenter.registerViewCallBack(this);
        //播放器的presenter
        playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.registerViewCallBack(this);
        updatePlayState(playerPresenter.isPlaying());
        initListener();
    }

    private void initListener() {

        playControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去控制播放器状态
                if (playerPresenter.isPlaying()) {
                    //正在播放就暂停
                    playerPresenter.pause();
                } else {
                    playerPresenter.play();
                }
            }
        });

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
    }

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        detailList = detailListView.findViewById(R.id.album_detail_list);
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
        return detailListView;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
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
            playControlTips.setText(playing ?
                    R.string.playing_tips_text : R.string.pause_tips_text);
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

    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
