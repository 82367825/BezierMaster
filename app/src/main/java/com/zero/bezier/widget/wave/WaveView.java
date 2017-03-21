package com.zero.bezier.widget.wave;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

/**
 * @author linzewu
 * @date 2016/5/29
 */
public class WaveView extends View {

    /**
     * 波峰
     */
    private float mWavePeak = 35f;
    /**
     * 波槽
     */
    private float mWaveTrough = 35f;
    /**
     * 水位
     */
    private float mWaveHeight = 250f;

    private Paint mPaint;

    private Path mPath;

    private int mWaterColor = 0xBB0000FF;

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mWaterColor);
    }

    private float mWidth, mHeight;

    private PointF mStart;

    private PointF mLeft1,mLeft2;

    private PointF mFirst, mSecond;

    private PointF mRight;

    private PointF mControlLeft1,mControlLeft2;

    private PointF mControlFirst;

    private PointF mControlSecond;

    private boolean mIsRunning = false;

    private boolean mHasInit = false;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (!mHasInit) {
            mWidth = w;
            mHeight = h;
            mHasInit = true;
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mIsRunning || !mHasInit)
            return;
        mPath.reset();
        mPath.moveTo(mLeft1.x, mLeft1.y);
        mPath.quadTo(mControlLeft1.x, mControlLeft1.y, mLeft2.x, mLeft2.y);
        mPath.quadTo(mControlLeft2.x, mControlLeft2.y, mFirst.x, mFirst.y);
        mPath.quadTo(mControlFirst.x, mControlFirst.y, mSecond.x, mSecond.y);
        mPath.quadTo(mControlSecond.x, mControlSecond.y, mRight.x, mRight.y);
        mPath.lineTo(mRight.x, mHeight);
        mPath.lineTo(mLeft1.x, mHeight);
        mPath.lineTo(mLeft1.x, mLeft1.y);
        canvas.drawPath(mPath, mPaint);
    }

    private void reset() {
        mStart = new PointF(-mWidth, mHeight - mWaveHeight);
        mLeft1 = new PointF(-mWidth, mHeight - mWaveHeight);
        mLeft2 = new PointF(-mWidth/2f, mHeight - mWaveHeight);
        mFirst = new PointF(0, mHeight - mWaveHeight);
        mSecond = new PointF(mWidth / 2f, mHeight - mWaveHeight);
        mRight = new PointF(mWidth, mHeight - mWaveHeight);
        mControlLeft1 = new PointF(mLeft1.x + mWidth / 4f, mLeft1.y + mWavePeak);
        mControlLeft2 = new PointF(mLeft2.x + mWidth / 4f, mLeft2.y - mWaveTrough);
        mControlFirst = new PointF(mFirst.x + mWidth / 4f, mFirst.y + mWavePeak);
        mControlSecond = new PointF(mSecond.x + mWidth / 4f, mSecond.y - mWaveTrough);
    }

    private class WaveHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ANIM_START) {
                reset();
                startAnim();
                mIsRunning = true;
            }
        }
    }

    private WaveHandler mWaveHandler = new WaveHandler();

    private static final int ANIM_START = 1;

    public void setRunning() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!mHasInit) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mWaveHandler.sendEmptyMessage(ANIM_START);
            }
        }).start();
    }

    private void startAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mStart.x, 0);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(Animation.INFINITE);
        //动画效果重复
//        valueAnimator.setRepeatMode(Animation.RESTART);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLeft1.x = (float) animation.getAnimatedValue();
                mLeft2 = new PointF(mLeft1.x + mWidth / 2f, mHeight - mWaveHeight);
                mFirst = new PointF(mLeft2.x + mWidth / 2f, mHeight - mWaveHeight);
                mSecond = new PointF(mFirst.x + mWidth / 2f, mHeight - mWaveHeight);
                mRight = new PointF(mSecond.x + mWidth / 2f, mHeight - mWaveHeight);
                mControlLeft1 = new PointF(mLeft1.x + mWidth / 4f, mLeft1.y + mWavePeak);
                mControlLeft2 = new PointF(mLeft2.x + mWidth / 4f, mLeft2.y - mWaveTrough);
                mControlFirst = new PointF(mFirst.x + mWidth / 4f, mFirst.y + mWavePeak);
                mControlSecond = new PointF(mSecond.x + mWidth / 4f, mSecond.y - mWaveTrough);
                invalidate();
            }
        });
        valueAnimator.start();
    }
}
