package com.example.wsins.xmly.presenters;

import com.example.wsins.xmly.base.BaseApplication;
import com.example.wsins.xmly.interfaces.IPlayerCallBack;
import com.example.wsins.xmly.interfaces.IPlayerPresenter;
import com.example.wsins.xmly.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {


    private List<IPlayerCallBack> iPlayerCallBacks = new ArrayList<>();

    private static final String TAG = "PlayerPresenter";
    private final XmPlayerManager xmPlayerManager;
    private Track currentTrack;
    private int currentIndex = 0;

    private PlayerPresenter() {
        xmPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告相关接口
        xmPlayerManager.addAdsStatusListener(this);
        //注册播放器相关接口
        xmPlayerManager.addPlayerStatusListener(this);

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

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {

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
    public boolean isPlay() {
        //返回当前是否正在播放
        return xmPlayerManager.isPlaying();

    }

    @Override
    public void registerViewCallBack(IPlayerCallBack iPlayerCallBack) {
        iPlayerCallBack.onTrackUpdate(currentTrack, currentIndex);
        if (!iPlayerCallBacks.contains(iPlayerCallBack)) {
            iPlayerCallBacks.add(iPlayerCallBack);
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
