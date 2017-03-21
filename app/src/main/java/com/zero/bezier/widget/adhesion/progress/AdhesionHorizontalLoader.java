package com.zero.bezier.widget.adhesion.progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.List;

/**
 * 粘合体进度条(水平)
 *
 * @author linzewu
 * @date 2016/5/23
 */
public class AdhesionHorizontalLoader extends View {

    /**
     * 宽度
     */
    private int mWidth;

    /**
     * 高度
     */
    private int mHeight;

    /**
     * 当前的静态圆半径
     */
    private float mCurrentStaticCircleRadius = 10f;

    /**
     * 静态圆变化半径的最大比率
     */
    private float mMaxStaticCircleRadiusScaleRate = 0.4f;

    /**
     * 静态圆个数
     */
    private int mStaticCircleCount = 5;

    /**
     * 圆与圆之间的间隔距离
     */
    private float mDivideWidth = 3 * mCurrentStaticCircleRadius;

    /**
     * 最大粘连长度
     */
    private float mMaxAdherentLength = 3.5f * mCurrentStaticCircleRadius;

    /**
     * 静态圆
     */
    private Circle mStaticCircle;

    /**
     * 动态圆
     */
    private Circle mDynamicCircle = new Circle();

    /**
     * 维护静态圆容器
     */
    private List<Circle> mStaticCircles = new ArrayList<Circle>();

    /**
     * 画笔
     */
    private Paint mPaint = new Paint();

    /**
     * 路径
     */
    private Path mPath = new Path();

    /**
     * 默认颜色
     */
    private int mColor = 0xFF4DB9FF;

    /**
     * 构造函数
     *
     * @param context
     */
    public AdhesionHorizontalLoader(Context context) {
        super(context);
        init();
    }

    /**
     * 构造函数
     *
     * @param context
     * @param attrs
     */
    public AdhesionHorizontalLoader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 构造函数
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public AdhesionHorizontalLoader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        
        /* 画笔 */
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        /* 宽度、高度 */
        mWidth = (int) ((mStaticCircleCount + 1) * (mCurrentStaticCircleRadius * 2 + mDivideWidth));
        mHeight = (int) (2 * mCurrentStaticCircleRadius * (1 + mMaxStaticCircleRadiusScaleRate));

        /* 动态圆 */
        mDynamicCircle.radius = mCurrentStaticCircleRadius * 3 / 4;
        ;
        mDynamicCircle.x = mDynamicCircle.radius;
        mDynamicCircle.y = mHeight / 2;
        
        /* 静态圆 */
        for (int i = 0; i < mStaticCircleCount; i++) {
            mStaticCircle = new Circle();
            mStaticCircle.radius = mCurrentStaticCircleRadius;
            mStaticCircle.x = (mStaticCircle.radius * 2 + mDivideWidth) * (i + 1);
            mStaticCircle.y = mHeight / 2;
            mStaticCircles.add(mStaticCircle);
        }
        
        /* 开始动画 */
        startAnim();
    }

    /**
     * 测量尺寸
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec 知识补充：
     *                          方法resolveSizeAndState()
     *                          用来创建最终的宽和高. 这个方法通过比较视图的期望大小返回一个合适的View.MeasureSpec值传入onMeasure()
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSizeAndState(mWidth, widthMeasureSpec, MeasureSpec.UNSPECIFIED), resolveSizeAndState(mHeight, heightMeasureSpec, MeasureSpec.UNSPECIFIED));
    }

    /**
     * 绘制视图
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {

        /* 动态圆 */
        canvas.drawCircle(mDynamicCircle.x, mDynamicCircle.y, mDynamicCircle.radius, mPaint);
        
        /* 静态圆 */
        for (int i = 0; i < mStaticCircleCount; i++) {
            mStaticCircle = mStaticCircles.get(i);
            
            /* 判断哪个圆可以作贝塞尔曲线 */
            if (doAdhere(i)) {
                canvas.drawCircle(mStaticCircle.x, mStaticCircle.y, mCurrentStaticCircleRadius, mPaint);
                // drawAdherentBody(canvas, i);
                Path path = Adhesion.drawAdhesionBody(mStaticCircle.x, mStaticCircle.y, 
                        mCurrentStaticCircleRadius, 45, mDynamicCircle.x, mDynamicCircle.y, mDynamicCircle.radius, 45);
                canvas.drawPath(path, mPaint);

            } else {
                canvas.drawCircle(mStaticCircle.x, mStaticCircle.y, mStaticCircle.radius, mPaint);
            }
        }
    }
    
    /**
     * 判断粘连范围，动态改变静态圆大小
     *
     * @param position
     * @return
     */
    private boolean doAdhere(int position) {

        mStaticCircle = mStaticCircles.get(position);
        
        /* 半径变化 */
        float distance = (float) Math.sqrt(Math.pow(mDynamicCircle.x - mStaticCircle.x, 2) + Math.pow(mDynamicCircle.y - mStaticCircle.y, 2));
        float scale = mMaxStaticCircleRadiusScaleRate - mMaxStaticCircleRadiusScaleRate * (distance / mMaxAdherentLength);
        mCurrentStaticCircleRadius = mStaticCircle.radius * (1 + scale);
       
        /* 判断是否可以作贝塞尔曲线 */
        if (distance < mMaxAdherentLength)
            return true;
        else
            return false;
    }

    /**
     * 开始动画
     */
    private void startAnim() {
         /* x方向 */
        ValueAnimator xValueAnimator = ValueAnimator.ofFloat(mDynamicCircle.x, mWidth - mDynamicCircle.radius);
        xValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        xValueAnimator.setDuration(2500);
        xValueAnimator.setRepeatCount(Animation.INFINITE);
        //动画效果重复
        xValueAnimator.setRepeatMode(Animation.REVERSE);
        xValueAnimator.start();
        xValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDynamicCircle.x = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    public void setColor(int color) {
        mColor = color;
        mPaint.setColor(mColor);
    }

    /**
     * 圆类
     */
    private class Circle {
        public float x;
        public float y;
        public float radius;
    }


}
