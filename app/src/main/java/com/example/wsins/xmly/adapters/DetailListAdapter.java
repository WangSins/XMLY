package com.example.wsins.xmly.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wsins.xmly.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.InnerHolder> {

    private List<Track> detailData = new ArrayList<>();
    //格式化时间
    private SimpleDateFormat updateDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat durationFormat = new SimpleDateFormat("mm:ss");

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_detail, viewGroup, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder innerHolder, int i) {
        //找到控件
        View itemview = innerHolder.itemView;
        //顺序ID
        TextView orderTv = itemview.findViewById(R.id.item_id_text);
        //标题
        TextView titleTv = itemview.findViewById(R.id.trick_title_text);
        //播放次数
        TextView playCountTv = itemview.findViewById(R.id.track_play_count_text);
        //时常
        TextView durationTv = itemview.findViewById(R.id.track_play_time_text);
        //更新日期
        TextView updateDateTv = itemview.findViewById(R.id.update_time_text);


        //设置数据
        Track track = detailData.get(i);
        orderTv.setText(i + "");
        titleTv.setText(track.getTrackTitle());
        playCountTv.setText(track.getPlayCount() + "");

        String durationText = durationFormat.format(track.getDuration()*1000);
        durationTv.setText(durationText);

        String updateTimeText = updateDateFormat.format(track.getUpdatedAt());
        updateDateTv.setText(updateTimeText);
    }

    @Override
    public int getItemCount() {
        return detailData.size();
    }

    public void setData(List<Track> tracks) {
        //清空原来数据
        detailData.clear();
        //添加新的数据
        detailData.addAll(tracks);
        //更新UI
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
