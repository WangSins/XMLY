package com.example.wsins.xmly.fragments;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wsins.xmly.R;
import com.example.wsins.xmly.adapters.AlbumListAdapter;
import com.example.wsins.xmly.base.BaseFragment;
import com.example.wsins.xmly.interfaces.ISubscriptionCallback;
import com.example.wsins.xmly.presenters.SubscriptionPresenter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback {

    private SubscriptionPresenter subscriptionPresenter;
    private TwinklingRefreshLayout overScrollView;
    private RecyclerView subListView;
    private AlbumListAdapter albumListAdapter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_subscription, container, false);

        overScrollView = rootView.findViewById(R.id.over_scroll_view);
        overScrollView.setEnableRefresh(false);
        overScrollView.setEnableLoadmore(false);

        subListView = rootView.findViewById(R.id.subscription_list);
        subListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        subListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        albumListAdapter = new AlbumListAdapter();
        subListView.setAdapter(albumListAdapter);

        subscriptionPresenter = SubscriptionPresenter.getsInstance();
        subscriptionPresenter.registerViewCallBack(this);
        subscriptionPresenter.getSubscriptionList();
        return rootView;
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {

    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albums) {
        //更新UI
        if (albumListAdapter != null) {
            albumListAdapter.setDate(albums);
        }
    }
}
