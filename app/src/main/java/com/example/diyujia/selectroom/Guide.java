package com.example.diyujia.selectroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diyujia on 2017/12/27.
 */

public class Guide extends Activity implements ViewPager.OnPageChangeListener{
    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;
    private Button bt_start;

    private ImageView[] dots;
    private int[] ids = {R.id.iv1,R.id.iv2,R.id.iv3};
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        initViews();
        initDots();
        initListeners();
    }

    void initDots(){
        dots = new ImageView[views.size()];
        for(int i=0;i<views.size();i++){
            dots[i]=(ImageView)findViewById(ids[i]);
        }
    }

    void initListeners(){
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Guide.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void initViews(){
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.guide1,null));
        views.add(inflater.inflate(R.layout.guide2,null));
        views.add(inflater.inflate(R.layout.guide3,null));
        vpAdapter = new ViewPagerAdapter(views,this);
        vp = (ViewPager)findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        vp.addOnPageChangeListener(this);
        bt_start = (Button)views.get(2).findViewById(R.id.guide_start);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for(int a=0;a<ids.length;a++){
            if(a == position){
                dots[a].setImageResource(R.drawable.page_indicator_focused);
            }else{
                dots[a].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
