package com.example.wsins.xmly.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by Sin on 2019/7/6
 */
public interface IHistoryDaoCallback {

    /**
     * 添加历史的结果
     *
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);

    /**
     * 删除历史的结果
     *
     * @param isSuccess
     */
    void onHistoryDel(boolean isSuccess);

    /**
     * 历史数据加载的结果
     *
     * @param lists
     */
    void onHistoryLoaded(List<Track> lists);

    /**
     * 历史记录清除的结果
     *
     * @param isSuccess
     */
    void onHistoryClean(boolean isSuccess);
}
