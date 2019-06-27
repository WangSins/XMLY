package com.example.wsins.xmly;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.wsins.xmly.adapters.AlbumListAdapter;
import com.example.wsins.xmly.adapters.SearchRecommendAdapter;
import com.example.wsins.xmly.base.BaseActivity;
import com.example.wsins.xmly.interfaces.ISearchCallback;
import com.example.wsins.xmly.presenters.SearchPresenter;
import com.example.wsins.xmly.utils.LogUtil;
import com.example.wsins.xmly.views.FlowTextLayout;
import com.example.wsins.xmly.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

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
    private RecyclerView resultListView;
    private AlbumListAdapter albumListAdapter;
    private FlowTextLayout recommendHotWordView;
    private InputMethodManager inputMethodManager;
    private ImageView delBtn;
    public static final int TIME_SHOW_IMM = 500;
    private RecyclerView searchRecommendList;
    private SearchRecommendAdapter recommendAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }


    private void initPresenter() {
        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

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
                if (TextUtils.isEmpty(s)) {
                    searchPresenter.getHotWord();
                    delBtn.setVisibility(View.GONE);
                } else {
                    delBtn.setVisibility(View.VISIBLE);
                    //触发联想查询
                    getSuggestWord(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputBox.setText("");
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
        recommendHotWordView.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                //第一步，把热词扔到输入框
                inputBox.setText(text);
                inputBox.setSelection(text.length());
                //第二步，发起搜索
                if (searchPresenter != null) {
                    searchPresenter.doSearch(text);
                }
                //改变UI状态
                if (uiLoader != null) {
                    uiLoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });


    }

    /**
     * 获取联想的关键词
     *
     * @param keyword
     */
    private void getSuggestWord(String keyword) {
        LogUtil.d(TAG, "getSuggestWord -- > " + keyword);
        if (searchPresenter != null) {
            searchPresenter.getRecommendWord(keyword);
        }
    }

    private void initView() {
        backBtn = this.findViewById(R.id.search_back);
        inputBox = this.findViewById(R.id.search_input);
        delBtn = this.findViewById(R.id.search_input_delete);
        inputBox.postDelayed(new Runnable() {
            @Override
            public void run() {
                inputBox.requestFocus();
                inputMethodManager.showSoftInput(inputBox, InputMethodManager.SHOW_IMPLICIT);
            }
        }, TIME_SHOW_IMM);
        searchBtn = this.findViewById(R.id.search_btn);
        resultContainer = this.findViewById(R.id.search_container);
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
        //显示热词
        recommendHotWordView = resultView.findViewById(R.id.recommend_hot_word_view);

        resultListView = resultView.findViewById(R.id.result_list_view);
        //设置布局管理器
        LinearLayoutManager resultLayoutManager = new LinearLayoutManager(this);
        resultListView.setLayoutManager(resultLayoutManager);
        //设置适配器
        albumListAdapter = new AlbumListAdapter();
        resultListView.setAdapter(albumListAdapter);
        //设置间距
        resultListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        //搜索推荐
        searchRecommendList = resultView.findViewById(R.id.search_recommend_list);
        //设置布局管理器
        LinearLayoutManager recommendLayoutManager = new LinearLayoutManager(this);
        searchRecommendList.setLayoutManager(recommendLayoutManager);
        //设置适配器
        recommendAdapter = new SearchRecommendAdapter();
        searchRecommendList.setAdapter(recommendAdapter);

        return resultView;
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {
        hideSuccessView();
        resultListView.setVisibility(View.VISIBLE);
        //隐藏软键盘
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
        hideSuccessView();
        recommendHotWordView.setVisibility(View.VISIBLE);
        if (uiLoader != null) {
            uiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
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
        recommendHotWordView.setTextContents(hotWords);

    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {

    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {
        //关键字的联想词
        LogUtil.d(TAG, "keyWordList size -- > " + keyWordList.size());
        if (recommendAdapter != null) {
            recommendAdapter.setData(keyWordList);
        }
        //控制UI的状态和隐藏显示
        if (uiLoader != null) {
            uiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //控制显示和隐藏
        hideSuccessView();
        searchRecommendList.setVisibility(View.VISIBLE);

    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (uiLoader != null) {
            uiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    private void hideSuccessView() {
        searchRecommendList.setVisibility(View.GONE);
        resultListView.setVisibility(View.GONE);
        recommendHotWordView.setVisibility(View.GONE);
    }

}
