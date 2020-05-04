package com.example.wsins.xmly.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by Sin on 2019/7/7
 */
public interface IHistoryCallBack {

    /**
     * 历史内容加载结果回调
     *
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);

    /**
     * 调用添加的时候，去通知UI结果
     *
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除历史记录的回调方法
     *
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);

    /**
     * 清除历史内容结果回调
     *
     * @param tracks
     */
    void onHistoriesClean(List<Track> tracks);
}
