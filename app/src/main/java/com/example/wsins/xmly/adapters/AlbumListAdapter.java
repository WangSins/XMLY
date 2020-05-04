package com.example.wsins.xmly.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wsins.xmly.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {

    private static final String TAG = "AlbumListAdapter";
    private List<Album> data = new ArrayList<>();
    private OnAlbumItemClickListener itemClickListener = null;
    private OnAlbumItemLongClickListener itemLongClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //加载View
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recommend, viewGroup, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder viewHolder, int i) {
        //设置数据
        viewHolder.itemView.setTag(i);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    int clickPosition = (int) v.getTag();
                    itemClickListener.onItemClick(clickPosition, data.get(clickPosition));
                }
                Log.d(TAG, "viewHolder.itemView click --> " + v.getTag());
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (itemLongClickListener != null) {
                    int clickPosition = (int) v.getTag();
                    itemLongClickListener.onItemLongClick(data.get(clickPosition));
                }
                //true表示消费掉该事件
                return false;
            }
        });
        viewHolder.setData(data.get(i));

    }

    @Override
    public int getItemCount() {
        //返回要显示的个数
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    public void setDate(List<Album> albumList) {
        if (data != null) {
            data.clear();
            data.addAll(albumList);
        }
        //更新一下UI
        notifyDataSetChanged();

    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到各个控件，设置数据
            //专辑的封面
            ImageView albumCoverIv = itemView.findViewById(R.id.album_cover);
            //title
            TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);
            //描述
            TextView albumDesrcTv = itemView.findViewById(R.id.album_description_tv);
            //播放数量
            TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            //专辑内容数量
            TextView albumContentCountTv = itemView.findViewById(R.id.album_content_size);

            long playCount = album.getPlayCount();
            String playCountString = "";
            if (playCount < 10000) {
                playCountString = String.valueOf(playCount);
            } else {
                double n = (double) playCount / 10000;
                playCountString = String.format("%.2f万", n);
            }

            albumTitleTv.setText(album.getAlbumTitle());
            albumDesrcTv.setText(album.getAlbumIntro());
            albumPlayCountTv.setText(playCountString);
            albumContentCountTv.setText(album.getIncludeTrackCount() + "集");

            String coverUrlLarge = album.getCoverUrlLarge();
            if (!TextUtils.isEmpty(coverUrlLarge)) {
                Picasso.with(itemView.getContext()).load(coverUrlLarge).into(albumCoverIv);
            } else {
                albumCoverIv.setImageResource(R.mipmap.logo);
            }

        }
    }

    public void setOnAlbumItemClickListener(OnAlbumItemClickListener listener) {
        this.itemClickListener = listener;

    }

    public interface OnAlbumItemClickListener {
        void onItemClick(int position, Album album);
    }

    public void setOnAlbumItemLongClickListener(OnAlbumItemLongClickListener listener) {
        this.itemLongClickListener = listener;

    }

    public interface OnAlbumItemLongClickListener {
        void onItemLongClick(Album album);
    }
}
