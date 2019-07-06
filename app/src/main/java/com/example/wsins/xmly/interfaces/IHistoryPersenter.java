package com.example.wsins.xmly.interfaces;

import com.example.wsins.xmly.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Created by Sin on 2019/7/7
 */
public interface IHistoryPersenter extends IBasePresenter<IHistoryCallback> {

    /**
     * 获取历史内容
     */
    void listHistories();

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
     * 清除历史内容
     */
    void cleanHistories();

}
