package com.example.wsins.xmly.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wsins.xmly.R;
import com.example.wsins.xmly.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InnerHoler> {
    private List<Track> mData = new ArrayList<>();
    private int playingIndex = 0;

    @Override
    public InnerHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_play_list, viewGroup, false);

        return new InnerHoler(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHoler innerHoler, int i) {
        //拿到数据
        Track track = mData.get(i);
        //设置数据
        TextView trackTitleTv = innerHoler.itemView.findViewById(R.id.track_title_tv);
        trackTitleTv.setText(track.getTrackTitle());
        //设置字体颜色
        trackTitleTv.setTextColor(
                BaseApplication.getAppContext().getResources().getColor(playingIndex == i ?
                        R.color.second_color : R.color.play_list_text_color));
        //找到播放状态的图标
        ImageView playingIconView = innerHoler.itemView.findViewById(R.id.pla_icon_iv);
        playingIconView.setVisibility(playingIndex == i ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Track> data) {
        //设置数据，更新列表
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();

    }

    public void setCurrentPlayPosition(int position) {
        playingIndex = position;
        notifyDataSetChanged();
    }

    public class InnerHoler extends RecyclerView.ViewHolder {
        public InnerHoler(@NonNull View itemView) {
            super(itemView);
        }
    }
}
