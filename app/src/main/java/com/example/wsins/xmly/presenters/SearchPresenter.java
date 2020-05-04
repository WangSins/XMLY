package com.example.wsins.xmly.presenters;

import android.support.annotation.Nullable;

import com.example.wsins.xmly.data.XimalayaApi;
import com.example.wsins.xmly.interfaces.ISearchCallBack;
import com.example.wsins.xmly.interfaces.ISearchPresenter;
import com.example.wsins.xmly.utils.Constants;
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
    private String currentKeyWord = null;
    private XimalayaApi ximalayaApi;
    private static final int DEFAULT_PAGE = 1;
    private int currentPage = DEFAULT_PAGE;
    private List<Album> searchResult = new ArrayList<>();

    private SearchPresenter() {
        ximalayaApi = XimalayaApi.getXimalayaApi();
    }

    public static SearchPresenter searchPresenter = null;

    public static SearchPresenter getSearchPresenter() {
        if (searchPresenter == null) {
            synchronized (SearchPresenter.class) {
                if (searchPresenter == null) {
                    searchPresenter = new SearchPresenter();
                }
            }
        }
        return searchPresenter;
    }

    private List<ISearchCallBack> callBacks = new ArrayList<>();

    @Override
    public void doSearch(String keyWord) {
        currentPage = DEFAULT_PAGE;
        searchResult.clear();
        //用于重新搜索
        //当网络不好的时候，用户会点击重新搜索
        this.currentKeyWord = keyWord;
        search(keyWord);
    }

    private void search(String keyWord) {
        ximalayaApi.searchByKeyWord(keyWord, currentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                searchResult.addAll(albums);
                if (albums != null) {
                    LogUtil.d(TAG, "albums size -- > " + albums.size());
                    if (isLoadMore) {
                        for (ISearchCallBack callback : callBacks) {
                            callback.onLoadMoreResult(searchResult, albums.size() != 0);
                        }
                        isLoadMore = false;
                    } else {
                        for (ISearchCallBack callBack : callBacks) {
                            callBack.onSearchResultLoaded(searchResult);
                        }
                    }
                } else {
                    LogUtil.d(TAG, "albums size is null..");
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "search errorCode -- > " + errorCode);
                LogUtil.d(TAG, "search errorMsg -- > " + errorMsg);
                for (ISearchCallBack callback : callBacks) {
                    if (isLoadMore) {
                        callback.onLoadMoreResult(searchResult, false);
                        isLoadMore = false;
                        currentPage--;
                    } else {
                        callback.onError(errorCode, errorMsg);
                    }
                }
            }
        });
    }

    @Override
    public void reSearch() {
        search(currentKeyWord);
    }

    private boolean isLoadMore = false;

    @Override
    public void loadMore() {
        //判断有没有必要进行加载更多
        if (searchResult.size() < Constants.COUNT_DEFAULT) {
            for (ISearchCallBack callback : callBacks) {
                callback.onLoadMoreResult(searchResult, false);
            }
        } else {
            isLoadMore = true;
            currentPage++;
            search(currentKeyWord);
        }
    }

    @Override
    public void getHotWord() {
        ximalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(@Nullable HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    LogUtil.d(TAG, "hotWords size -- > " + hotWords.size());
                    for (ISearchCallBack callback : callBacks) {
                        callback.onHotWordLoaded(hotWords);
                    }
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
    public void getRecommendWord(String keyWord) {
        ximalayaApi.getSuggestWord(keyWord, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(@Nullable SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    LogUtil.d(TAG, "keyWordList size -- > " + keyWordList.size());
                    for (ISearchCallBack callback : callBacks) {
                        callback.onRecommendWordLoaded(keyWordList);
                    }
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
    public void registerViewCallBack(ISearchCallBack iSearchCallBack) {
        if (!callBacks.contains(iSearchCallBack)) {
            callBacks.add(iSearchCallBack);
        }
    }

    @Override
    public void unRegisterViewCallBack(ISearchCallBack iSearchCallBack) {
        callBacks.remove(iSearchCallBack);
    }
}
