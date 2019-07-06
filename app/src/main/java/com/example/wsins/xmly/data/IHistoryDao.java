package com.example.wsins.xmly.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Created by Sin on 2019/7/6
 */
public interface IHistoryDao {

    /**
     * 设置回调接口
     *
     * @param callback
     */
    void setCallback(IHistoryDaoCallback callback);

    /**
     * 添加历史
     *
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除历史
     *
     * @param track
     */
    void delHistory(Track track);

    /**
     * 清除历史
     */
    void cleanHistory();

    /**
     * 获取历史内容
     */
    void listHistory();
}
