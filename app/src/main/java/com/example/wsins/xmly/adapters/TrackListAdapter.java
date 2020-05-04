package com.example.wsins.xmly.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wsins.xmly.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.InnerHolder> {

    private List<Track> detailData = new ArrayList<>();
    //格式化时间
    private SimpleDateFormat updateDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat durationFormat = new SimpleDateFormat("mm:ss");
    private ItemClickListener itemClickListener = null;
    private ItemLongClickListener itemLongClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_detail, viewGroup, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder innerHolder, final int i) {
        //找到控件
        final View itemView = innerHolder.itemView;
        //顺序ID
        TextView orderTv = itemView.findViewById(R.id.item_id_text);
        //标题
        TextView titleTv = itemView.findViewById(R.id.trick_title_text);
        //播放次数
        TextView playCountTv = itemView.findViewById(R.id.track_play_count_text);
        //时常
        TextView durationTv = itemView.findViewById(R.id.track_play_time_text);
        //更新日期
        TextView updateDateTv = itemView.findViewById(R.id.update_time_text);


        //设置数据
        final Track track = detailData.get(i);

        long playCount = track.getPlayCount();
        String playCountString = "";
        if (playCount < 10000) {
            playCountString = String.valueOf(playCount);
        } else {
            double n = (double) playCount / 10000;
            playCountString = String.format("%.2f万", n);
        }

        orderTv.setText((i + 1) + "");
        titleTv.setText(track.getTrackTitle());
        playCountTv.setText(playCountString);

        String durationText = durationFormat.format(track.getDuration() * 1000);
        durationTv.setText(durationText);

        String updateTimeText = updateDateFormat.format(track.getUpdatedAt());
        updateDateTv.setText(updateTimeText);


        //设置item的点击事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
//                Toast.makeText(v.getContext(), "you click " + i + " item", Toast.LENGTH_SHORT).show();
                if (itemClickListener != null) {
                    //参数需要有列表和位置
                    itemClickListener.onItemClick(detailData, i);
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (itemLongClickListener != null) {
                    itemLongClickListener.onItemLongClick(track);
                }
                return true;
            }
        });

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

    public void setItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(List<Track> detailData, int i);
    }

    public void setItemLongClickListener(ItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    public interface ItemLongClickListener {
        void onItemLongClick(Track track);
    }
}
