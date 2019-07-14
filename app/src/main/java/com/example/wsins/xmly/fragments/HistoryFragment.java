package com.example.wsins.xmly.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.wsins.xmly.PlayerActivity;
import com.example.wsins.xmly.R;
import com.example.wsins.xmly.adapters.TrackListAdapter;
import com.example.wsins.xmly.base.BaseFragment;
import com.example.wsins.xmly.interfaces.IHistoryCallback;
import com.example.wsins.xmly.presenters.HistoryPresenter;
import com.example.wsins.xmly.presenters.PlayerPresenter;
import com.example.wsins.xmly.views.ConfirmCheckBoxDialog;
import com.example.wsins.xmly.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class HistoryFragment extends BaseFragment implements IHistoryCallback, TrackListAdapter.ItemClickListener, TrackListAdapter.ItemLongClickListener, ConfirmCheckBoxDialog.OnDialogActionClickListener {

    private UILoader uiLoader;
    private TwinklingRefreshLayout overScrollView;
    private RecyclerView historyListView;
    private TrackListAdapter trackListAdapter = null;
    private HistoryPresenter historyPresenter;
    private Track currentClickHistoryItem = null;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_history, container, false);
        if (uiLoader == null) {
            uiLoader = new UILoader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }

                @Override
                protected View getEmptyView() {
                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                    TextView tipsView = emptyView.findViewById(R.id.empty_view_tips_tv);
                    tipsView.setText(R.string.no_history_content_tips_text);
                    return emptyView;
                }
            };
        }
        if (uiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) uiLoader.getParent()).removeView(uiLoader);
        }
        historyPresenter = HistoryPresenter.getInstance();
        historyPresenter.registerViewCallBack(this);
        if (uiLoader != null) {
            uiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        historyPresenter.listHistories();
        rootView.addView(uiLoader);
        return rootView;
    }

    private View createSuccessView(ViewGroup container) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_history, null);
        overScrollView = itemView.findViewById(R.id.over_scroll_view);
        overScrollView.setEnableRefresh(false);
        overScrollView.setEnableLoadmore(false);

        historyListView = itemView.findViewById(R.id.history_list);
        historyListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        trackListAdapter = new TrackListAdapter();
        trackListAdapter.setItemClickListener(this);
        trackListAdapter.setItemLongClickListener(this);
        historyListView.setAdapter(trackListAdapter);
        historyListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 2);
                outRect.right = UIUtil.dip2px(view.getContext(), 2);
            }
        });
        return itemView;
    }

    @Override
    public void onHistoriesLoaded(List<Track> tracks) {
        if (tracks == null || tracks.size() == 0) {
            if (uiLoader != null) {
                uiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        } else {
            if (uiLoader != null && trackListAdapter != null) {
                //更新数据
                trackListAdapter.setData(tracks);
                uiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {

    }

    @Override
    public void onHistoriesClean(List<Track> tracks) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (historyPresenter != null) {
            historyPresenter.unRegisterViewCallBack(this);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int i) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(detailData, i);
        //跳转到播放器界面
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Track track) {
        this.currentClickHistoryItem = track;
        //删除历史
        ConfirmCheckBoxDialog dialog = new ConfirmCheckBoxDialog(getActivity());
        dialog.setOnDialogActionClickListener(this);
        dialog.show();
    }

    @Override
    public void onCancelClick() {
        //不用做
    }

    @Override
    public void onConfirmClick(boolean isChecked) {
        if (currentClickHistoryItem != null && historyPresenter != null) {
            if (isChecked) {
                historyPresenter.cleanHistories();
            } else {
                historyPresenter.delHistory(currentClickHistoryItem);
            }
        }
    }
}
