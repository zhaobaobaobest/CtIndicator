package com.ct.ctindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ct
 * create at 2019/4/29
 * description: 新版仿微博头部指示器，使用请注明作者
 */

public class CtIndicatorNew extends LinearLayout {
    private int heigh = 0;
    private List<TextView> tvs = new ArrayList<>();
    private ViewPager vp;
    private int start=0,end = 0;
    private boolean isSlide = false;//是否滑动中
    private TextView chooseTv;

    //自定义属性
    private int duration = 500;//动画时间
    private int textColor = Color.GRAY;//字体颜色
    private int textSize = 28;//字体大小 sp
    private int chooseColor = Color.RED;//选中颜色
    private int textPadding = dp2px(8);//textview 左右间隔 dp
    private int indicatorHeigh = dp2px(4);//指示器高度 dp
    private int indicatorPadding = dp2px(16);//指示器距离左右的宽度 dp

    public CtIndicatorNew(Context context) {
        this(context,null);
    }

    public CtIndicatorNew(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public CtIndicatorNew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.ct_indicator,defStyleAttr,0);
        duration = a.getInt(R.styleable.ct_indicator_duration,500);
        textColor = a.getColor(R.styleable.ct_indicator_text_color,Color.GRAY);
        chooseColor = a.getColor(R.styleable.ct_indicator_choose_color,Color.RED);
        //getDimension 最终值返回的是换算后的px
        textSize = (int) a.getDimension(R.styleable.ct_indicator_text_size,28f);
        textPadding = (int) a.getDimension(R.styleable.ct_indicator_text_padding,dp2px(8));
        indicatorHeigh = (int) a.getDimension(R.styleable.ct_indicator_indicator_heigh,dp2px(4));
        indicatorPadding = (int) a.getDimension(R.styleable.ct_indicator_indicator_padding,dp2px(16));
        a.recycle();
        initView();
    }

    private void initView()
    {
        this.setOrientation(LinearLayout.HORIZONTAL);
    }

    public void setSelect(int index)
    {
        if(vp==null || tvs==null || index>=vp.getAdapter().getCount())
            return;
        if(chooseTv!=null) {
            chooseTv.setTextColor(textColor);
            chooseTv.setTag(false);
        }
        chooseTv = tvs.get(index);
        chooseTv.setTextColor(chooseColor);
        chooseTv.setTag(true);

        vp.setCurrentItem(index,false);
    }

    public void setTitleAndVp(final List<String> titleList, final ViewPager vp)
    {
        if(titleList==null || vp==null || (titleList.size()!=vp.getAdapter().getCount()))
        {
            return;
        }
        for(int i=0;i<titleList.size();i++)
        {
            String title = titleList.get(i);
            TextView tv = new TextView(getContext());
            tv.setText(title);
            tv.setTextColor(textColor);
            tv.setTextSize(px2dp(textSize));//设置的是sp
            tv.setTag(false);
            tv.setTag(R.id.index,i);
            tv.setPadding(textPadding,0,textPadding,0);
            tv.setGravity(Gravity.CENTER);
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChoose = (boolean) v.getTag();
                    int index = (int) v.getTag(R.id.index);
                    if(!isChoose)
                    {
                        chooseTv.setTextColor(textColor);
                        chooseTv.setTag(false);
                        chooseTv = (TextView) v;
                        chooseTv.setTextColor(chooseColor);
                        chooseTv.setTag(true);
                        vp.setCurrentItem(index,titleList.size()>2?false:true);
                    }
                }
            });
            tvs.add(tv);
            addView(tv,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
        }
        chooseTv = tvs.get(vp.getCurrentItem());
        chooseTv.setTextColor(chooseColor);
        chooseTv.setTag(true);

        //初始viewpage
        this.vp = vp;
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int index = vp.getCurrentItem();
            private int minEnd = tvs.get(vp.getCurrentItem()).getRight()-tvs.get(vp.getCurrentItem()).getPaddingRight()-indicatorPadding;//中间记录
            private int minStart = tvs.get(vp.getCurrentItem()).getLeft()+tvs.get(vp.getCurrentItem()).getPaddingLeft()+indicatorPadding;
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if(v!=0) {
                    isSlide =true;
                    if (index == i) {//向左滑动
                        //滑动到一半的时候就走完了
                        if(v<0.5) {
                            end = (int) (minEnd + tvs.get(i).getWidth() * v * 2);//移动的就是宽的百分比
                        }else
                        {
                            end = minEnd + tvs.get(i).getWidth();
                            start = (int) (minStart + (tvs.get(i).getWidth() * (2*v-1)));
                        }
                    } else {//向右滑动
                        if(v>0.5) {
                            start = (int) (minStart - (tvs.get(i).getWidth() * (1 - v) * 2));
                        }
                        else
                        {
                            start = minStart - tvs.get(i).getWidth();
                            end = (int) (minEnd - (tvs.get(i).getWidth() *(1-2*v)));
                        }
                    }
                }
                else
                {
                    isSlide = false;
                    chooseTv.setTextColor(textColor);
                    chooseTv.setTag(false);
                    chooseTv = tvs.get(vp.getCurrentItem());
                    chooseTv.setTextColor(chooseColor);
                    chooseTv.setTag(true);
                    //容错
                    index = vp.getCurrentItem();
                    minStart = tvs.get(vp.getCurrentItem()).getLeft()+tvs.get(vp.getCurrentItem()).getPaddingLeft()+indicatorPadding;
                    minEnd = tvs.get(vp.getCurrentItem()).getRight()-tvs.get(vp.getCurrentItem()).getPaddingRight()-indicatorPadding;
                }
                invalidate();
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {
                isSlide =true;
                if(i==1)
                {
                    //容错
                    isSlide = false;
                    index = vp.getCurrentItem();
                    //位置重置 ,确定min max值
                    minStart = tvs.get(vp.getCurrentItem()).getLeft()+tvs.get(vp.getCurrentItem()).getPaddingLeft()+indicatorPadding;
                    minEnd = tvs.get(vp.getCurrentItem()).getRight()-tvs.get(vp.getCurrentItem()).getPaddingRight()-indicatorPadding;
                    invalidate();
                }
                else if(i==0) {
                    isSlide = false;
                    chooseTv.setTextColor(textColor);
                    chooseTv.setTag(false);
                    chooseTv = tvs.get(vp.getCurrentItem());
                    chooseTv.setTextColor(chooseColor);
                    chooseTv.setTag(true);
                    //容错
                    index = vp.getCurrentItem();
                    minStart = tvs.get(vp.getCurrentItem()).getLeft()+tvs.get(vp.getCurrentItem()).getPaddingLeft()+indicatorPadding;
                    minEnd = tvs.get(vp.getCurrentItem()).getRight()-tvs.get(vp.getCurrentItem()).getPaddingRight()-indicatorPadding;

                    invalidate();
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        heigh = MeasureSpec.getSize(heightMeasureSpec);
    }

    //viewgroup下onDraw无效
    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(vp!=null) {
            //测量成功才能获得宽高
            if (!isSlide) {
                start = tvs.get(vp.getCurrentItem()).getLeft() + tvs.get(vp.getCurrentItem()).getPaddingLeft()+indicatorPadding;
                end = tvs.get(vp.getCurrentItem()).getRight() - tvs.get(vp.getCurrentItem()).getPaddingRight()-indicatorPadding;
            }
            // 保存画布
            canvas.save();
            //设置画笔
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(chooseColor);
            paint.setStyle(Paint.Style.FILL);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(start, heigh - indicatorHeigh, end, heigh, indicatorHeigh/2, indicatorHeigh/2, paint);
            } else {
                canvas.drawRect(start, heigh - indicatorHeigh, end, heigh, paint);
            }

            // 恢复画布
            canvas.restore();
        }
        super.dispatchDraw(canvas);
    }

    private int dp2px(int dp)
    {
        return (int) (getContext().getResources().getDisplayMetrics().density*dp+0.5f);
    }

    private int px2dp(int px)
    {
        return (int) (px/getContext().getResources().getDisplayMetrics().density+0.5f);
    }
}
