package com.example.wsins.xmly.presenters;

import com.example.wsins.xmly.base.BaseApplication;
import com.example.wsins.xmly.data.HistoryDao;
import com.example.wsins.xmly.data.IHistoryDao;
import com.example.wsins.xmly.data.IHistoryDaoCallback;
import com.example.wsins.xmly.interfaces.IHistoryCallback;
import com.example.wsins.xmly.interfaces.IHistoryPersenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Sin on 2019/7/7
 */
public class HistoryPresenter implements IHistoryPersenter, IHistoryDaoCallback {

    private List<IHistoryCallback> callbacks = new ArrayList<>();

    private final IHistoryDao historyDao;

    private HistoryPresenter() {
        historyDao = new HistoryDao();
        historyDao.setCallback(this);
    }

    private static HistoryPresenter sInstance = null;

    public static HistoryPresenter getInstance() {
        if (sInstance == null) {
            synchronized (HistoryPresenter.class) {
                if (sInstance == null) {
                    sInstance = new HistoryPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void listHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (historyDao != null) {
                    historyDao.listHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void addHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (historyDao != null) {
                    historyDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (historyDao != null) {
                    historyDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void cleanHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (historyDao != null) {
                    historyDao.cleanHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallBack(IHistoryCallback iHistoryCallback) {
        if (!callbacks.contains(iHistoryCallback)) {
            callbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unRegisterViewCallBack(IHistoryCallback iHistoryCallback) {
        callbacks.remove(iHistoryCallback);
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        //nothing to do.
        listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        //nothing to do.
        listHistories();
    }

    @Override
    public void onHistoryLoaded(final List<Track> lists) {
        //通知UI更新数据
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallback callback : callbacks) {
                    callback.onHistoriesLoaded(lists);
                }
            }
        });
    }

    @Override
    public void onHistoryClean(boolean isSuccess) {
        //nothing to do.
        listHistories();
    }
}
