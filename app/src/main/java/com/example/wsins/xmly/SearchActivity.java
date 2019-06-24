package com.example.wsins.xmly;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.wsins.xmly.adapters.AlbumListAdapter;
import com.example.wsins.xmly.base.BaseActivity;
import com.example.wsins.xmly.interfaces.ISearchCallback;
import com.example.wsins.xmly.presenters.SearchPresenter;
import com.example.wsins.xmly.utils.LogUtil;
import com.example.wsins.xmly.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sin on 2019/6/22
 */
public class SearchActivity extends BaseActivity implements ISearchCallback {

    private static final String TAG = "SearchActivity";
    private ImageView backBtn;
    private EditText inputBox;
    private View searchBtn;
    private FrameLayout resultContainer;
    private SearchPresenter searchPresenter;
    private UILoader uiLoader;
    private RecyclerView resultListview;
    private AlbumListAdapter albumListAdapter;
    //    private FlowTextLayout flowTextLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }


    private void initPresenter() {
        searchPresenter = SearchPresenter.getSearchPresenter();
        //注册UI更新的接口
        searchPresenter.registerViewCallBack(this);
        //去拿热词
        searchPresenter.getHotWord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchPresenter != null) {
            //干掉UI更新的接口
            searchPresenter.unRegisterViewCallBack(this);
            searchPresenter = null;
        }
    }

    private void initEvent() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去调用搜索逻辑
                String keyword = inputBox.getText().toString().trim();
                if (searchPresenter != null) {
                    searchPresenter.doSearch(keyword);
                    uiLoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });
        inputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtil.d(TAG, "uiLoader -- > " + s);
                LogUtil.d(TAG, "start -- > " + start);
                LogUtil.d(TAG, "before -- > " + before);
                LogUtil.d(TAG, "count -- > " + count);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        uiLoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                if (searchPresenter != null) {
                    searchPresenter.reSearch();
                    uiLoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });
//        flowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
//            @Override
//            public void onItemClick(String text) {
//                Toast.makeText(SearchActivity.this, text, Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void initView() {
        backBtn = this.findViewById(R.id.search_back);
        inputBox = this.findViewById(R.id.search_input);
        searchBtn = this.findViewById(R.id.search_btn);
        resultContainer = this.findViewById(R.id.search_container);
//        flowTextLayout = this.findViewById(R.id.flow_text_layout);
        if (uiLoader == null) {
            uiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }
            };

        }
        if (uiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) uiLoader.getParent()).removeView(uiLoader);
        }
        resultContainer.addView(uiLoader);
    }

    /**
     * 创建数据请求成功的View
     *
     * @return
     */
    private View createSuccessView() {
        View resultView = LayoutInflater.from(this).inflate(R.layout.search_result_layout, null);
        resultListview = resultView.findViewById(R.id.result_list_view);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        resultListview.setLayoutManager(layoutManager);
        //设置适配器
        albumListAdapter = new AlbumListAdapter();
        resultListview.setAdapter(albumListAdapter);
        return resultView;
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {
        //隐藏软键盘
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(inputBox.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (result != null) {
            if (result.size() == 0) {
                //数据为空
                if (uiLoader != null) {
                    uiLoader.updateStatus(UILoader.UIStatus.EMPTY);
                }
            } else {
                //如果数据不为空，那么就设置数据
                albumListAdapter.setDate(result);
                uiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }

        }
    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {
        LogUtil.d(TAG, "hotWordList size -- > " + hotWordList.size());
        List<String> hotWords = new ArrayList<>();
        hotWords.clear();
        for (HotWord hotWord : hotWordList) {
            String searchWord = hotWord.getSearchword();
            hotWords.add(searchWord);
        }
        LogUtil.d(TAG, "hotWords size -- > " + hotWords.size());
        Collections.sort(hotWords);
        //更新UI
//        flowTextLayout.setTextContents(hotWords);

    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {

    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {

    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (uiLoader != null) {
            uiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

}
