package com.example.wsins.xmly;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.example.wsins.xmly.adapters.IndicatorAdapter;
import com.example.wsins.xmly.adapters.MainContentAdapter;
import com.example.wsins.xmly.utils.LogUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    private MagicIndicator magicIndicator;
    private ViewPager contentPager;

    private IndicatorAdapter indicatorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initEvent() {
        indicatorAdapter.setOnIndicatorTabClickListener(new IndicatorAdapter.OnIndicatorTabClickListener() {
            @Override
            public void onTabClick(int i) {
                LogUtil.d(TAG, "click index is --> " + i);
                if (contentPager != null) {
                    contentPager.setCurrentItem(i);
                }
            }
        });

    }

    private void initView() {
        magicIndicator = this.findViewById(R.id.main_indicator);
        magicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建indicator的适配器
        indicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(indicatorAdapter);

        //ViewPager
        contentPager = this.findViewById(R.id.content_pager);

        //创建内容适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);
        contentPager.setAdapter(mainContentAdapter);
        //把ViewPager和indicator绑定到一起
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, contentPager);
    }

}
