package com.example.wsins.xmly.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * Created by Sin on 2019/6/30
 */
public interface ISubDao {

    void setCallBack(ISubDaoCallBack callBack);

    /**
     * 添加专辑订阅
     *
     * @param album
     */
    void addAlbum(Album album);

    /**
     * 删除订阅内容
     *
     * @param album
     */
    void delAlbum(Album album);

    /**
     * 获取订阅内容
     */
    void listAlbum();
}
