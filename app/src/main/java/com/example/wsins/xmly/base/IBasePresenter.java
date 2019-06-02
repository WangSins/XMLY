package com.example.wsins.xmly.base;

public interface IBasePresenter<T> {

    /**
     * 注册回调接口
     * @param t
     */
    void registerViewCallBack(T t);

    /**
     * 取消注册
     * @param t
     */
    void unRegisterViewCallBack(T t);
}
