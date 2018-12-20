package com.example.wsins.xmly;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.wsins.xmly.adapters.IndicatorAdapter;
import com.example.wsins.xmly.adapters.MainContentAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    private MagicIndicator magicIndicator;
    private ViewPager contentPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


//        Map<String, String> map = new HashMap<String, String>();
//        CommonRequest.getCategories(map, new IDataCallBack<CategoryList>() {
//            @Override
//            public void onSuccess(@Nullable CategoryList categoryList) {
//                List<Category> categories = categoryList.getCategories();
//                if (categories != null) {
//                    int size = categories.size();
//                    Log.d(TAG, "categories size <--" + size);
//                    for (Category category : categories) {
//                        //Log.d(TAG, "category --> " + category.getCategoryName());
//                        LogUtil.d(TAG, "category --> " + category.getCategoryName());
//                    }
//                }
//
//            }
//
//            @Override
//            public void onError(int i, String s) {
//                //Log.d(TAG, "error code -- " + i + " error msg ==> " + s);
//                LogUtil.d(TAG,"error code -- " + i + " error msg ==> " + s);
//
//            }
//        });

    }

    private void initView() {
        magicIndicator = this.findViewById(R.id.main_indicator);
        magicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建indicator的适配器
        IndicatorAdapter adapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(adapter);

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
