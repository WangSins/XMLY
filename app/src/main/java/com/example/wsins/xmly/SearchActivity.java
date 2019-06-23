package com.example.wsins.xmly;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.wsins.xmly.base.BaseActivity;
import com.example.wsins.xmly.interfaces.ISearchCallback;
import com.example.wsins.xmly.presenters.SearchPresenter;
import com.example.wsins.xmly.utils.LogUtil;
import com.example.wsins.xmly.views.FlowTextLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
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
    private FlowTextLayout flowTextLayout;

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

            }
        });
        inputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtil.d(TAG, "content -- > " + s);
                LogUtil.d(TAG, "start -- > " + start);
                LogUtil.d(TAG, "before -- > " + before);
                LogUtil.d(TAG, "count -- > " + count);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initView() {
        backBtn = this.findViewById(R.id.search_back);
        inputBox = this.findViewById(R.id.search_input);
        searchBtn = this.findViewById(R.id.search_btn);
        resultContainer = this.findViewById(R.id.search_container);
        flowTextLayout = this.findViewById(R.id.flow_text_layout);

    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {

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
        //更新UI
        flowTextLayout.setTextContents(hotWords);

    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {

    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {

    }
}
