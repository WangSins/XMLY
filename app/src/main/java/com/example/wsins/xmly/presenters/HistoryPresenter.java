package com.example.wsins.xmly.presenters;

import com.example.wsins.xmly.base.BaseApplication;
import com.example.wsins.xmly.data.HistoryDao;
import com.example.wsins.xmly.data.IHistoryDao;
import com.example.wsins.xmly.data.IHistoryDaoCallBack;
import com.example.wsins.xmly.interfaces.IHistoryCallBack;
import com.example.wsins.xmly.interfaces.IHistoryPresenter;
import com.example.wsins.xmly.utils.Constants;
import com.example.wsins.xmly.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Sin on 2019/7/7
 * 历史记录最大100条。如果超过100条记录那么把最前面添加的删除，再把当前的历史添加进去。
 */
public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallBack {

    private static final String TAG = "HistoryPresenter";
    private List<IHistoryCallBack> callBacks = new ArrayList<>();

    private final IHistoryDao historyDao;
    private List<Track> currentHistories = null;
    private Track currentAddTrack = null;

    private HistoryPresenter() {
        historyDao = new HistoryDao();
        historyDao.setCallback(this);
        listHistories();
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

    private boolean isDoDelAsOutOfSize = false;

    @Override
    public void addHistory(final Track track) {
        //需要去判断是否>=100条记录
        if (currentHistories != null && currentHistories.size() >= Constants.MAX_HISTORY_COUNT) {
            isDoDelAsOutOfSize = true;
            this.currentAddTrack = track;
            //先不能添加，先删除最前的一条记录，再添加
            delHistory(currentHistories.get(currentHistories.size() - 1));
        } else {
            doAddHistory(track);
        }

    }

    private void doAddHistory(final Track track) {
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
    public void registerViewCallBack(IHistoryCallBack iHistoryCallBack) {
        if (!callBacks.contains(iHistoryCallBack)) {
            callBacks.add(iHistoryCallBack);
        }
    }

    @Override
    public void unRegisterViewCallBack(IHistoryCallBack iHistoryCallBack) {
        callBacks.remove(iHistoryCallBack);
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        //nothing to do.
        listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        //nothing to do.
        if (isDoDelAsOutOfSize && currentAddTrack != null) {
            isDoDelAsOutOfSize = false;
            //添加当前数据进到数据库里面去
            addHistory(currentAddTrack);
        } else {
            listHistories();
        }
    }

    @Override
    public void onHistoryLoaded(final List<Track> lists) {
        this.currentHistories = lists;
        LogUtil.d(TAG, "histories size --> " + lists.size());
        //通知UI更新数据
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallBack callback : callBacks) {
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
