package com.example.wsins.xmly.presenters;

import android.support.annotation.Nullable;

import com.example.wsins.xmly.api.XimalayApi;
import com.example.wsins.xmly.interfaces.ISearchCallback;
import com.example.wsins.xmly.interfaces.ISearchPresenter;
import com.example.wsins.xmly.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private static final String TAG = "SearchPresenter";
    //当前搜索关键字
    private String currentKeyword = null;
    private XimalayApi ximalayApi;
    private static final int DEFAULT_PAGE = 1;
    private int currentPage = DEFAULT_PAGE;

    private SearchPresenter() {
        ximalayApi = XimalayApi.getXimalayApi();
    }

    public static SearchPresenter searchPresenter = null;

    public static SearchPresenter getSearchPresenter() {
        if (searchPresenter != null) {
            synchronized (SearchPresenter.class) {
                if (searchPresenter != null) {
                    searchPresenter = new SearchPresenter();
                }
            }
        }
        return searchPresenter;
    }

    private List<ISearchCallback> callbacks = new ArrayList<>();

    @Override
    public void doSearch(String keyword) {
        //用于重新搜索
        //当网络不好的时候，用户会点击重新搜索
        this.currentKeyword = keyword;
        search(keyword);
    }

    private void search(String keyword) {
        ximalayApi.searchByKeyWord(keyword, currentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                if (albums != null) {
                    LogUtil.d(TAG, "albums size -- > " + albums.size());
                } else {
                    LogUtil.d(TAG, "albums size is null..");
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "search errorCode -- > " + errorCode);
                LogUtil.d(TAG, "search errorMsg -- > " + errorMsg);

            }
        });
    }

    @Override
    public void reSearch() {
        search(currentKeyword);
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getHotWord() {
        ximalayApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(@Nullable HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    LogUtil.d(TAG, "hotWords size -- > " + hotWords.size());
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "getHotWord errorCode -- > " + errorCode);
                LogUtil.d(TAG, "getHotWord errorMsg -- > " + errorMsg);
            }
        });

    }

    @Override
    public void getRecommendWord(String keyword) {
        ximalayApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(@Nullable SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    LogUtil.d(TAG, "keyWordList size -- > " + keyWordList.size());
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "getRecommendWord errorCode -- > " + errorCode);
                LogUtil.d(TAG, "getRecommendWord errorMsg -- > " + errorMsg);
            }
        });

    }

    @Override
    public void registerViewCallBack(ISearchCallback iSearchCallback) {
        if (callbacks.contains(iSearchCallback)) {
            callbacks.add(iSearchCallback);
        }
    }

    @Override
    public void unRegisterViewCallBack(ISearchCallback iSearchCallback) {
        callbacks.add(iSearchCallback);
    }
}
