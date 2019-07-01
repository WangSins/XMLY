package com.example.wsins.xmly.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by Sin on 2019/6/30
 */
public interface ISubDaoCallback {

    /**
     * 添加的结果回调方法
     *
     * @param issuccess
     */
    void onAddResult(boolean issuccess);

    /**
     * 删除的结果回调方法
     *
     * @param issuccess
     */
    void onDelResult(boolean issuccess);

    /**
     * 加载的结果
     *
     * @param result
     */
    void onSubListLoaded(List<Album> result);
}
