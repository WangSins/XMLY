package com.example.wsins.xmly.presenters;

import com.example.wsins.xmly.base.BaseApplication;
import com.example.wsins.xmly.data.ISubDaoCallback;
import com.example.wsins.xmly.data.SubscriptionDao;
import com.example.wsins.xmly.interfaces.ISubscriptionCallback;
import com.example.wsins.xmly.interfaces.ISubscriptionPresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Sin on 2019/7/1
 */
public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallback {

    private final SubscriptionDao subscriptionDao;
    private Map<Long, Album> datas = new HashMap<>();
    private List<ISubscriptionCallback> callbacks = new ArrayList<>();

    private SubscriptionPresenter() {
        subscriptionDao = SubscriptionDao.getInstance();
        subscriptionDao.setCallback(this);
        listSubscriptions();
    }

    private void listSubscriptions() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                //只调用，不处理结果
                if (subscriptionDao != null) {
                    subscriptionDao.listAlbum();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private static SubscriptionPresenter sInstance = null;

    private SubscriptionPresenter getsInstance() {
        if (sInstance == null) {
            synchronized (SubscriptionPresenter.class) {
                if (sInstance == null) {
                    sInstance = new SubscriptionPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void addSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (subscriptionDao != null) {
                    subscriptionDao.addAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (subscriptionDao != null) {
                    subscriptionDao.delAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubscriptionList() {
        listSubscriptions();
    }

    @Override
    public boolean isSubscription(Album album) {
        Album result = datas.get(album.getId());
        return result == null;
    }

    @Override
    public void registerViewCallBack(ISubscriptionCallback iSubscriptionCallback) {
        if (!callbacks.contains(iSubscriptionCallback)) {
            callbacks.add(iSubscriptionCallback);
        }
    }

    @Override
    public void unRegisterViewCallBack(ISubscriptionCallback iSubscriptionCallback) {
        callbacks.remove(iSubscriptionCallback);
    }

    @Override
    public void onAddResult(final boolean isSuccess) {
        //添加结果的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : callbacks) {
                    callback.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDelResult(final boolean isSuccess) {
        //删除订阅的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : callbacks) {
                    callback.onDeleteResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onSubListLoaded(final List<Album> result) {
        //加载数据的回调
        for (Album album : result) {
            datas.put(album.getId(), album);
        }
        //通知UI更新
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : callbacks) {
                    callback.onSubscriptionsLoaded(result);
                }
            }
        });
    }
}
