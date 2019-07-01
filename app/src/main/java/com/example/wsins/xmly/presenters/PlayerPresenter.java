package com.example.wsins.xmly.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.wsins.xmly.data.XimalayaApi;
import com.example.wsins.xmly.base.BaseApplication;
import com.example.wsins.xmly.interfaces.IPlayerCallBack;
import com.example.wsins.xmly.interfaces.IPlayerPresenter;
import com.example.wsins.xmly.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.*;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {


    private List<IPlayerCallBack> iPlayerCallBacks = new ArrayList<>();

    private static final String TAG = "PlayerPresenter";
    private final XmPlayerManager xmPlayerManager;
    private Track currentTrack;
    private static final int DEFAULT_PLAY_INDEX = 0;
    private int currentIndex = DEFAULT_PLAY_INDEX;
    private final SharedPreferences playModeSP;
    private XmPlayListControl.PlayMode currentPlayMode = PLAY_MODEL_LIST;
    private boolean isReverse = false;
    /*
    PLAY_MODEL_LIST (默认)
    PLAY_MODEL_LIST_LOOP
    PLAY_MODEL_RANDOM
    PLAY_MODEL_SINGLE_LOOP
     */
    public static final int PLAY_MODEL_LIST_INT = 0;
    public static final int PLAY_MODEL_LIST_LOOP_INT = 1;
    public static final int PLAY_MODEL_RANDOM_INT = 2;
    public static final int PLAY_MODEL_SINGLE_LOOP_INT = 3;

    public static String PLAY_MODE_SP_NAME = "PlayMode";
    public static String PLAY_MODE_SP_KEY = "currentPlayMode";
    private int currentProgressPosition = 0;
    private int progressDuration = 0;

    private PlayerPresenter() {
        xmPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告相关接口
        xmPlayerManager.addAdsStatusListener(this);
        //注册播放器相关接口
        xmPlayerManager.addPlayerStatusListener(this);
        //需要记录当前播放模式
        playModeSP = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODE_SP_NAME, Context.MODE_PRIVATE);


    }

    private static PlayerPresenter sPlayerPresenter;

    public static PlayerPresenter getPlayerPresenter() {
        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class) {
                if (sPlayerPresenter == null) {
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }

    private boolean isPlayListSet = false;

    public void setPlayList(List<Track> list, int playIndex) {
        if (xmPlayerManager != null) {
            xmPlayerManager.setPlayList(list, playIndex);
            isPlayListSet = true;
            currentTrack = list.get(playIndex);
            currentIndex = playIndex;
        } else {
            LogUtil.d(TAG, "xmPlayerManager is null");
        }
    }


    @Override
    public void play() {
        if (isPlayListSet) {
            xmPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (xmPlayerManager != null) {
            xmPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        //播放上一个节目
        if (xmPlayerManager != null) {
            xmPlayerManager.playPre();
        }

    }

    @Override
    public void playNext() {
        //播放下一个节目
        if (xmPlayerManager != null) {
            xmPlayerManager.playNext();
        }

    }

    /**
     * 判断是否有播放的节目列表
     *
     * @return
     */
    public boolean hasPlayList() {
        return isPlayListSet;
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (xmPlayerManager != null) {
            currentPlayMode = mode;
            xmPlayerManager.setPlayMode(mode);
            //通知UI更新播放模式
            for (IPlayerCallBack iPlayerCallBack : iPlayerCallBacks) {
                iPlayerCallBack.onPlayModeChange(mode);
            }
            //保存到SP
            SharedPreferences.Editor edit = playModeSP.edit();
            edit.putInt(PLAY_MODE_SP_KEY, getIntByPlayMode(mode));
            edit.commit();

        }
    }

    private int getIntByPlayMode(XmPlayListControl.PlayMode mode) {
        switch (mode) {
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
        }
        return PLAY_MODEL_LIST_INT;

    }

    private XmPlayListControl.PlayMode getPlayModeByInt(int index) {
        switch (index) {
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;
        }
        return PLAY_MODEL_LIST;

    }

    @Override
    public void getPlayList() {
        if (xmPlayerManager != null) {
            List<Track> playList = xmPlayerManager.getPlayList();
            for (IPlayerCallBack iPlayerCallBack : iPlayerCallBacks) {
                iPlayerCallBack.onListLoaded(playList);
            }
        }

    }

    @Override
    public void playByIndex(int index) {
        //切换播放器到第index位置进行播放
        if (xmPlayerManager != null) {
            xmPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        //更新播放器进度
        xmPlayerManager.seekTo(progress);


    }

    @Override
    public boolean isPlaying() {
        //返回当前是否正在播放
        return xmPlayerManager.isPlaying();

    }

    @Override
    public void reversePlayList() {
        //把播放列表翻转
        List<Track> playList = xmPlayerManager.getPlayList();
        Collections.reverse(playList);
        isReverse = !isReverse;
        //第一个参数时播放列表，第二个参数时开始播放的下标
        //反转后下标位置 = 总内容个数 - 1 - 当前下标位置
        currentIndex = playList.size() - 1 - currentIndex;
        xmPlayerManager.setPlayList(playList, currentIndex);
        //更新UI
        currentTrack = (Track) xmPlayerManager.getCurrSound();
        for (IPlayerCallBack iPlayerCallBack : iPlayerCallBacks) {
            iPlayerCallBack.onListLoaded(playList);
            iPlayerCallBack.onTrackUpdate(currentTrack, currentIndex);
            iPlayerCallBack.updateListOrder(isReverse);
        }

    }

    @Override
    public void playByAlbumId(long id) {
        //1.获取到专辑的列表内容
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                List<Track> tracks = trackList.getTracks();
                if (trackList != null && tracks.size() > 0) {
                    xmPlayerManager.setPlayList(tracks, DEFAULT_PLAY_INDEX);
                    isPlayListSet = true;
                    currentTrack = tracks.get(DEFAULT_PLAY_INDEX);
                    currentIndex = DEFAULT_PLAY_INDEX;
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "errorCode -- >" + errorCode);
                LogUtil.d(TAG, "errorMsg -- >" + errorMsg);
                Toast.makeText(BaseApplication.getAppContext(), "数据请求失败...", Toast.LENGTH_SHORT).show();
            }
        }, (int) id, 1);
        //2.把专辑内容设置给播放器
        //3.播放
    }


    @Override
    public void registerViewCallBack(IPlayerCallBack iPlayerCallBack) {
        if (!iPlayerCallBacks.contains(iPlayerCallBack)) {
            iPlayerCallBacks.add(iPlayerCallBack);
        }
        //更新之前，让UI的Pager有数据
        getPlayList();
        //通知当前的节目
        iPlayerCallBack.onTrackUpdate(currentTrack, currentIndex);
        iPlayerCallBack.onProgressChange(currentProgressPosition, progressDuration);
        //更新播放状态
        handlerPlayStatus(iPlayerCallBack);
        //从sp里拿
        int modeIndex = playModeSP.getInt(PLAY_MODE_SP_KEY, PLAY_MODEL_LIST_INT);
        currentPlayMode = getPlayModeByInt(modeIndex);
        iPlayerCallBack.onPlayModeChange(currentPlayMode);
    }

    private void handlerPlayStatus(IPlayerCallBack iPlayerCallBack) {
        int playerStatus = xmPlayerManager.getPlayerStatus();
        //根据状态调用接口的方法
        if (PlayerConstants.STATE_STARTED == playerStatus) {
            iPlayerCallBack.onPlayStart();
        } else {
            iPlayerCallBack.onPlayPause();
        }
    }

    @Override
    public void unRegisterViewCallBack(IPlayerCallBack iPlayerCallBack) {
        iPlayerCallBacks.remove(iPlayerCallBack);

    }
    //================ 广告相关回调 start==============

    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG, "onStartGetAdsInfo....");

    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG, "onGetAdsInfo....");

    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG, "onAdsStartBuffering....");

    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG, "onAdsStopBuffering....");

    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG, "onStartPlayAds....");

    }


    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG, "onCompletePlayAds....");

    }

    @Override
    public void onError(int what, int extra) {
        LogUtil.d(TAG, "onError what -> " + what + "，extra -> " + extra);

    }
    //================ 广告相关回调 end==============

    //================ 播放器状态相关回调 end==============
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG, "onPlayStart...");
        for (IPlayerCallBack iPlayerCallBack : iPlayerCallBacks) {
            iPlayerCallBack.onPlayStart();
        }

    }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG, "onPlayPause...");
        for (IPlayerCallBack iPlayerCallBack : iPlayerCallBacks) {
            iPlayerCallBack.onPlayPause();
        }

    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG, "onPlayStop...");
        for (IPlayerCallBack iPlayerCallBack : iPlayerCallBacks) {
            iPlayerCallBack.onPlayStop();
        }

    }

    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG, "onSoundPlayComplete...");

    }

    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG, "onSoundPrepared...");
        LogUtil.d(TAG, "current status -> " + xmPlayerManager.getPlayerStatus());

        xmPlayerManager.setPlayMode(currentPlayMode);
        if (xmPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            //播放器准备完毕，可以去播放
            xmPlayerManager.play();
        }

    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        LogUtil.d(TAG, "onSoundSwitch...");
        if (lastModel != null) {
            LogUtil.d(TAG, "lastModel -> " + lastModel.getKind());
        }
        LogUtil.d(TAG, "onSoundSwitch ->" + curModel.getKind());
        //curModel代表的是当前播放的内容
        //通过getKind()获取他是什么类型的
        //track表示track类型
        //第一张写法
        //if ("track".equals(curModel.getKind())) {
        //    Track currentTrack = (Track) curModel;
        //    LogUtil.d(TAG, "currentTrackTitle -> " + currentTrack.getTrackTitle());
        //}
        //第二种写法
        currentIndex = xmPlayerManager.getCurrentIndex();
        if (curModel instanceof Track) {
            Track crTrack = (Track) curModel;
            currentTrack = crTrack;
            //更新UI
            for (IPlayerCallBack iPlayerCallBack : iPlayerCallBacks) {
                iPlayerCallBack.onTrackUpdate(currentTrack, currentIndex);
            }
        }
    }

    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG, "onBufferingStart...");

    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG, "onBufferingStop...");

    }

    @Override
    public void onBufferProgress(int progress) {
        LogUtil.d(TAG, "onBufferProgress..." + progress);

    }

    @Override
    public void onPlayProgress(int currentPos, int duration) {
        this.currentProgressPosition = currentPos;
        this.progressDuration = duration;
        //单位是毫秒
//        LogUtil.d(TAG, "onPlayProgress...currentPos -> " + currentPos + "，duration -> " + duration);
        for (IPlayerCallBack iPlayerCallBack : iPlayerCallBacks) {
            iPlayerCallBack.onProgressChange(currentPos, duration);
        }

    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG, "onError -> " + e);
        return false;
    }
    //================ 播放器状态相关回调 end==============
}
