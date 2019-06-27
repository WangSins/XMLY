package com.example.wsins.xmly.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wsins.xmly.R;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sin on 2019/6/26
 */
public class SearchRecommendAdapter extends RecyclerView.Adapter<SearchRecommendAdapter.InnerHolder> {

    private List<QueryResult> datas = new ArrayList<>();
    private ItemClickListener itemClickListener = null;

    @NonNull
    @Override
    public SearchRecommendAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_recommend, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecommendAdapter.InnerHolder holder, int position) {
        TextView text = holder.itemView.findViewById(R.id.search_recommend_item);
        final QueryResult queryResult = datas.get(position);
        text.setText(queryResult.getKeyword());
        //设置点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(queryResult.getKeyword());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * 设置数据
     *
     * @param keyWordList
     */
    public void setData(List<QueryResult> keyWordList) {
        datas.clear();
        datas.addAll(keyWordList);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(View itemView) {
            super(itemView);
        }
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;

    }

    public interface ItemClickListener {
        void onItemClick(String keyword);
    }
}
