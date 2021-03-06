package com.example.wsins.xmly.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallBack {

    /**
     * 开始播放
     */
    void onPlayStart();

    /**
     * 播放暂停
     */
    void onPlayPause();

    /**
     * 播放停止
     */
    void onPlayStop();

    /**
     * 播放错误
     */
    void onPlayError(Track track);

    /**
     * 上一首
     */
    void onPrePlay(Track track);

    /**
     * 播放列表加载完成
     *
     * @param list
     */
    void onListLoaded(List<Track> list);

    /**
     * 播放模式改变了
     *
     * @param playMode
     */
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    /**
     * 进度条改变
     *
     * @param currentIndex
     * @param total
     */
    void onProgressChange(int currentIndex, int total);

    /**
     * 广告正在加载
     */
    void onAdLoading();

    /**
     * 广告结束
     */
    void onAdFinished();

    /**
     * 更新当前节目
     *
     * @param track
     */
    void onTrackUpdate(Track track, int playIndex);

    /**
     * 通知UI更新播放列表的顺序文字和图标
     *
     * @param isReverse
     */
    void updateListOrder(boolean isReverse);
}
