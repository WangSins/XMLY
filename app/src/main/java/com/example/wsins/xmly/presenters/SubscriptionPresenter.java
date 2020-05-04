package com.example.wsins.xmly.presenters;

import com.example.wsins.xmly.base.BaseApplication;
import com.example.wsins.xmly.data.ISubDaoCallBack;
import com.example.wsins.xmly.data.SubscriptionDao;
import com.example.wsins.xmly.interfaces.ISubscriptionCallBack;
import com.example.wsins.xmly.interfaces.ISubscriptionPresenter;
import com.example.wsins.xmly.utils.Constants;
import com.example.wsins.xmly.utils.LogUtil;
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
public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallBack {

    private static final String TAG = "SubscriptionPresenter";
    private final SubscriptionDao subscriptionDao;
    private Map<Long, Album> datas = new HashMap<>();
    private List<ISubscriptionCallBack> callbacks = new ArrayList<>();

    private SubscriptionPresenter() {
        subscriptionDao = SubscriptionDao.getInstance();
        subscriptionDao.setCallBack(this);
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

    public static SubscriptionPresenter getsInstance() {
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
        //判断当前订阅数量，不超过100
        if (datas.size() >= Constants.MAX_SUB_COUNT) {
            //给出提示
            for (ISubscriptionCallBack callback : callbacks) {
                callback.onSubFull();
            }
            return;
        }
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
        //不为空，表示已经订阅
        return result != null;
    }

    @Override
    public void registerViewCallBack(ISubscriptionCallBack iSubscriptionCallBack) {
        if (!callbacks.contains(iSubscriptionCallBack)) {
            callbacks.add(iSubscriptionCallBack);
        }
    }

    @Override
    public void unRegisterViewCallBack(ISubscriptionCallBack iSubscriptionCallBack) {
        callbacks.remove(iSubscriptionCallBack);
    }

    @Override
    public void onAddResult(final boolean isSuccess) {
        LogUtil.d(TAG, "listSubscriptions after add...");
        listSubscriptions();
        //添加结果的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "update ui for add result.");
                for (ISubscriptionCallBack callback : callbacks) {
                    callback.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDelResult(final boolean isSuccess) {
        listSubscriptions();
        //删除订阅的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallBack callback : callbacks) {
                    callback.onDeleteResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onSubListLoaded(final List<Album> result) {
        //加载数据的回调
        datas.clear();
        for (Album album : result) {
            datas.put(album.getId(), album);
        }
        //通知UI更新
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallBack callback : callbacks) {
                    callback.onSubscriptionsLoaded(result);
                }
            }
        });
    }
}
