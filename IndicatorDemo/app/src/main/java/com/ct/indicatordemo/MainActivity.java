package com.ct.indicatordemo;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ct.ctindicator.CtIndicatorNew;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private CtIndicatorNew civ;
    private ViewPager vp;
    private MyPagerAdapter adapter;
    private List<View> viewList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vp = findViewById(R.id.vp);
        for(int i=0;i<3;i++)
        {
            TextView tv = new TextView(this);
            tv.setText("页面"+i);
            viewList.add(tv);
            titles.add("标题中间"+i);
        }
        adapter = new MyPagerAdapter(viewList);
        vp.setAdapter(adapter);
        civ = findViewById(R.id.indicator);
        civ.setTitleAndVp(titles,vp);
    }
}
