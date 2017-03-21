package com.zero.bezier.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author linzewu
 * @date 2016/6/7
 */
public class BezierTestView extends View {

    private PointF mPointF1;

    private PointF mPointF2;

    private PointF mControl;

    public BezierTestView(Context context) {
        super(context);
        init();
    }

    public BezierTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BezierTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xFF412129);
        mPath = new Path();
        mPointF1 = new PointF(80, 200);
        mPointF2 = new PointF(550, 200);
        mControl = new PointF(100, 200);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mControl.x = event.getX();
                mControl.y = event.getY();
                invalidate();
                break;
        }
        return true;
    }

    private Paint mPaint;

    private Path mPath;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        mPath.moveTo(mPointF1.x, mPointF1.y);
        mPath.quadTo(mControl.x, mControl.y, mPointF2.x, mPointF2.y);
        mPath.lineTo(mPointF1.x, mPointF1.y);
        canvas.drawPath(mPath, mPaint);
    }
}
