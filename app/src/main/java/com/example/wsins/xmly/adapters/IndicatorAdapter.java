package com.example.wsins.xmly.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.example.wsins.xmly.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

public class IndicatorAdapter extends CommonNavigatorAdapter {

    private final String[] titles;
    private OnIndicatorTabClickListener onTabClickListener;

    public IndicatorAdapter(Context context) {
        titles = context.getResources().getStringArray(R.array.indicator_title);

    }

    @Override
    public int getCount() {
        if (titles != null) {
            return titles.length;
        }
        return 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int i) {
        //创建View
        SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
        //设置一般情况下的颜色为灰色
        simplePagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));
        //色湖之选中情况下的颜色为黑色
        simplePagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        //单位为sp
        simplePagerTitleView.setTextSize(18);
        //设置要显示的内容
        simplePagerTitleView.setText(titles[i]);
        //设置title的点击事件，这里的话，如果点击title，那么久选中下面的ViewPager到对应的i中去
        //也就是说，当我们点击了title的时候，下面ViewPager会对应着i内容进行切换
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果index不一样切换View的内容
                if (onTabClickListener != null){
                    onTabClickListener.onTabClick(i);
                }
            }
        });
        //把这个创建好的View返回回去
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setColors(Color.parseColor("#ffffff"));
        return linePagerIndicator;
    }

    public void setOnIndicatorTabClickListener(OnIndicatorTabClickListener listener) {
        this.onTabClickListener = listener;

    }

    public interface OnIndicatorTabClickListener {
        void onTabClick(int i);
    }
}
