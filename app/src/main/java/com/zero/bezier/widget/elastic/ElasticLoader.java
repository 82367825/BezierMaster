package com.zero.bezier.widget.elastic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * 弹性球Loader
 * @author linzewu
 * @date 2016/5/23
 */
public class ElasticLoader extends View {
    /**
     * 宽度
     */
    private int mWidth;
    /**
     * 高度
     */
    private int mHeight;
    
    /**
     * 默认静态球个数
     */
    private static final int DEFAULT_NUM = 3;
    /**
     * 默认静态球颜色
     */
    private static final int DEFAULT_STATIC_BALL_COLOR = 0xFFFFFFFF;
    /**
     * 默认弹性球颜色
     */
    private static final int DEFAULT_ELASTIC_BALL_COLOR = 0xFF323423;
    /**
     * 默认半径
     */
    private static final float DEFAULT_RADIUS = 25;
    /**
     * 静态球个数
     */
    private int mStaticBallNum = DEFAULT_NUM;
    /**
     * 静态球颜色
     */
    private int mStaticBallColor = DEFAULT_STATIC_BALL_COLOR;
    /**
     * 弹性球颜色
     */
    private int mElasticBallColor = DEFAULT_ELASTIC_BALL_COLOR;
    /**
     * 球之间的距离
     */
    private float mDistanceBall = DEFAULT_RADIUS * 3;
    /**
     * 球的半径
     */
    private float mRadius = DEFAULT_RADIUS;
    
    
    /**
     * 边距
     */
    private float mMarginTop = DEFAULT_RADIUS;
    private float mMarginBottom = DEFAULT_RADIUS;
    private float mMarginLeft = DEFAULT_RADIUS;
    private float mMarginRight = DEFAULT_RADIUS;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 弹性圆
     */
    private ElasticBall mElasticCircle;
    /**
     * 静态圆
     */
    private List<ElasticBall> mCircles;
    
    public ElasticLoader(Context context) {
        super(context);
        init();
    }

    public ElasticLoader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ElasticLoader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        /* 画笔 */
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mMarginLeft = mRadius;
        mMarginRight = mRadius;
        mMarginTop = mRadius;
        mMarginBottom = mRadius;
        mWidth = (int) (mMarginLeft + mMarginRight + mStaticBallNum * mRadius * 2 + mDistanceBall
                * (mStaticBallNum - 1));
        mHeight = (int) (mRadius * 2 + mMarginTop + mMarginBottom);
        /* 初始化圆 */
        mCircles = new ArrayList<ElasticBall>();
        /* 静态圆 */
        for (int i = 0 ; i < mStaticBallNum ; i++) {
            ElasticBall circle = new ElasticBall(mMarginLeft + mDistanceBall * i + mRadius + 2 * 
                    i * mRadius, mMarginTop + mRadius, mRadius);
            mCircles.add(circle);
        }
        /* 弹性圆 */
        mElasticCircle = new ElasticBall(mCircles.get(0).x, mCircles.get(0).y+200, mRadius);
        startAnim();
    }

    /**
     * 测量尺寸
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSizeAndState(mWidth, widthMeasureSpec, MeasureSpec.UNSPECIFIED), resolveSizeAndState(mHeight, heightMeasureSpec, MeasureSpec.UNSPECIFIED));
    }
    
    public void setStaticBallNum(int num) {
        this.mStaticBallNum = num;
    }
    
    public void setStaticBallColor(int color) {
        this.mStaticBallColor = color;
    }

    public void setElasticBallColor(int color) {
        this.mElasticBallColor = color;
    }

    
    @Override
    protected void onDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mPaint.setColor(mStaticBallColor);
        for (int i = 0 ; i < mCircles.size() ; i++) {
            canvas.drawCircle(mCircles.get(i).x , mCircles.get(i).y, mRadius, mPaint);
        }
        mPaint.setColor(mElasticBallColor);
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
    }
    
    private Path mPath;
    
    private int mPos = 0;
    private int mNext = 1;

    /**
     * 启动动画
     */
    private void startAnim() {
        mElasticCircle = new ElasticBall(mCircles.get(mPos).x, mCircles.get(mPos).y, mRadius);
        mElasticCircle.setDuration(1000);
        PointF endPoint = new PointF(mCircles.get(mNext).x, mCircles.get(mNext).y);
        mElasticCircle.startElasticAnim(endPoint, new ElasticBall.ElasticBallInterface() {
            @Override
            public void onChange(Path path) {
                mPath = new Path(path);
                invalidate();
            }

            @Override
            public void onFinish() {
                mPos ++;
                if (mPos >= mCircles.size()-1)
                    mPos = 0;
                mNext = mPos + 1;
                startAnim();
            }
        });
    }
    
}
