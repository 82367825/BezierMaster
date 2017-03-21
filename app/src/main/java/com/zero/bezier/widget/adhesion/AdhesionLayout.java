package com.zero.bezier.widget.adhesion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.RelativeLayout;


/**
 * 粘合布局
 * @author linzewu
 * @date 2016/5/23
 */
public class AdhesionLayout extends RelativeLayout{

    /**
     * 头部圆
     */
    private Circle mHeaderCircle = new Circle();

    /**
     * 尾部圆
     */
    private Circle mFooterCircle = new Circle();

    /**
     * 画笔
     */
    private Paint mPaint = new Paint();

    /**
     * 画贝塞尔曲线的Path对象
     */
    private Path mPath = new Path();

    /**
     * 默认粘连的最大长度
     */
    private float mMaxAdherentLength = 100;

    /**
     * 头部圆缩小时不能小于这个最小半径
     */
    private float mMinHeaderCircleRadius = 4;

    /**
     * 用户添加的视图（可以不添加）
     */
    private View mView;

    /**
     * 监听粘连是否断掉的监听器
     */
    private OnAdherentListener mOnAdherentListener;

    /**
     * 是否粘连着
     */
    private boolean isAdherent = true;

    /**
     * 粘连的颜色
     */
    private int mColor;

    /**
     * 头部圆的当前半径
     */
    private float mCurrentRadius;

    /**
     * 是否第一次onSizeChanged
     */
    private boolean isFirst = true;

    /**
     * 是否正在进行动画中
     */
    private boolean isAnim = false;

    /**
     * 是否允许可以扯断
     */
    private boolean isSnap = true;

    /**
     * 本View宽度、高度
     */
    private int mWidth;
    private int mHeight;

    /**
     * 本View的左上角x、y
     */
    private float mX;
    private float mY;

    /**
     * 记录按下的x、y
     */
    float mDownX;
    float mDownY;

    /**
     * 按下的时候，记录此刻尾部圆的圆心x、y
     */
    float mDownFooterCircleX;
    float mDownFooterCircleY;


    /**
     * 构造函数
     *
     * @param context
     */
    public AdhesionLayout(Context context) {
        super(context);
        init();
    }

    /**
     * 构造函数
     *
     * @param context
     * @param attrs
     */
    public AdhesionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 构造函数
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AdhesionLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 初始化
     */
    private void init() {

        // 透明背景
        setBackgroundColor(Color.TRANSPARENT);

        // 设置颜色
        mColor = Color.rgb(247,82,49);

        // 设置画笔
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
    }


    /**
     * 当视图组大小发生变化   只调用一次
     * 关于onSizeChanged（）的调用问题，原则是只有视图大小改变的时候调用，但是由于是首次初始化
     * 因此，在视图初始化的时候，会调用一次，即mWidth mHeight只会赋值一次
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isFirst && w > 0 && h > 0) {
            isFirst = false;
            mView = getChildAt(0);
            mWidth = w;
            mHeight = h;
            mX = getX();
            mY = getY();
            reset();
        }
    }

    /**
     * 重置所有参数
     */
    public void reset() {
        //还原视图宽高为默认值
        setWidthAndHeight(mWidth, mHeight);  //设置宽高
        mCurrentRadius = mHeaderCircle.radius = mFooterCircle.radius = (float)(Math.min(mWidth,mHeight) / 2) - 2;
        mHeaderCircle.x = mFooterCircle.x = mWidth / 2;  //使得头部圆置于视图组的中点
        mHeaderCircle.y = mFooterCircle.y = mHeight / 2; //使得尾部圆置于视图的的中心
        if (mView != null) {                 //让子视图置于左上角
            mView.setX(0);
            mView.setY(0);
        }
        isAnim = false;

    }

    /**
     * 绘制方法
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        //绘制头部圆
        canvas.drawCircle(mHeaderCircle.x, mHeaderCircle.y, mCurrentRadius, mPaint);
        //绘制尾部圆
        canvas.drawCircle(mFooterCircle.x, mFooterCircle.y, mFooterCircle.radius, mPaint);
        if (isAdherent)
            drawBezier(canvas);
    }

    /**
     * 画贝塞尔曲线
     *
     * 知识点补充：
     * 1、atan : 反正切值    因为tan(atanA)=A，互为反函数 所以 atanA = 角度值
     *    double atan(double d)  返回对应角度的反正切值
     * 2、sin : 正弦值
     *    double sin(double d)  返回对应角度的正弦值
     * 3、cos : 余弦值
     *    double cos(double d)  返回对应角度的余弦值
     * @param canvas
     */
    private void drawBezier(Canvas canvas) {
        
        /* 求三角函数 */
        //atan为角度值
        float atan = (float) Math.atan((mFooterCircle.y - mHeaderCircle.y) / (mFooterCircle.x - mHeaderCircle.x));
        //获取sin、cos值
        float sin = (float) Math.sin(atan);
        float cos = (float) Math.cos(atan);
        
        /* 四个点 */
        float headerX1 = mHeaderCircle.x - mCurrentRadius * sin;
        float headerY1 = mHeaderCircle.y + mCurrentRadius * cos;

        float headerX2 = mHeaderCircle.x + mCurrentRadius * sin;
        float headerY2 = mHeaderCircle.y - mCurrentRadius * cos;

        float footerX1 = mFooterCircle.x - mFooterCircle.radius * sin;
        float footerY1 = mFooterCircle.y + mFooterCircle.radius * cos;

        float footerX2 = mFooterCircle.x + mFooterCircle.radius * sin;
        float footerY2 = mFooterCircle.y - mFooterCircle.radius * cos;

        /**
         * 操作点
         */
        float anchorX = ( mHeaderCircle.x + mFooterCircle.x ) / 2;
        float anchorY = ( mHeaderCircle.y + mFooterCircle.y ) / 2;
    
        /* 画贝塞尔曲线 */
        mPath.reset();
        mPath.moveTo(headerX1, headerY1);
        mPath.quadTo(anchorX, anchorY, footerX1, footerY1);
        mPath.lineTo(footerX2, footerY2);
        mPath.quadTo(anchorX, anchorY, headerX2, headerY2);
        mPath.lineTo(headerX1, headerY1);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 触摸方法
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        /**
         * 如果正在执行动画，不接受任何触摸事件
         */
        if (isAnim)
            return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /**
                 * 重新设置布局大小： 填充父布局
                 * 注意：只有在点击以及移动的时候，才会让视图暂时变为填充满父布局，
                 *      普通情况下，视图大小为默认设置的宽高，不会填充父布局，这样避免了其他控件的点击事件被屏蔽
                 */
                setWidthAndHeight(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                /**
                 * 把两个圆的坐标由原来的坐标转化为填充父布局之后的坐标
                 */
                mHeaderCircle.x = mX + mHeaderCircle.x;
                mHeaderCircle.y = mY + mHeaderCircle.y;
                mFooterCircle.x = mX + mFooterCircle.x;
                mFooterCircle.y = mY + mFooterCircle.y;
                mDownX = mX + event.getX();
                mDownY = mY + event.getY();
                mDownFooterCircleX = mFooterCircle.x;
                mDownFooterCircleY = mFooterCircle.y;
                if (mView != null) {
                    mView.setX(mFooterCircle.x - mWidth / 2);
                    mView.setY(mFooterCircle.y - mHeight / 2);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mFooterCircle.x = mDownFooterCircleX + (event.getX() - mDownX);
                mFooterCircle.y = mDownFooterCircleY + (event.getY() - mDownY);
                if (mView != null) {
                    mView.setX(mFooterCircle.x - mWidth / 2);
                    mView.setY(mFooterCircle.y - mHeight / 2);
                }
                doAdhere();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isAnim = true;
                if (isAdherent)
                    startAnim();
                else if (mOnAdherentListener != null) {
                    mFooterCircle.radius = 0;
                    mOnAdherentListener.onDismiss();
                }
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 处理粘连效果逻辑
     *
     * 知识补充：
     *        Math.sqrt(x)   返回x的平方根
     *        Math.pow(x,y)  返回x的y次方
     */
    private void doAdhere() {
        float distance = (float) Math.sqrt(Math.pow(mFooterCircle.x - mHeaderCircle.x, 2) + Math.pow(mFooterCircle.y - mHeaderCircle.y, 2));
        float scale = 1 - distance / mMaxAdherentLength;
        mCurrentRadius = Math.max(mHeaderCircle.radius * scale, mMinHeaderCircleRadius);

        /**
         * 如果拉扯距离大于设定的最大距离并且此时设置了可以扯断
         */
        if (distance > mMaxAdherentLength && isSnap) {
            isAdherent = false;
            mCurrentRadius = 0;
        }
        else
            isAdherent = true;
    }

    /**
     * 开始粘连动画
     */
    private void startAnim() {
        
        /* x方向 */
        ValueAnimator xValueAnimator = ValueAnimator.ofFloat(mFooterCircle.x, mX + mWidth / 2);
        xValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFooterCircle.x = (float) animation.getAnimatedValue();
                //动画执行的同时不断调用，不断刷新视图
                invalidate();
            }
        });

        /* y方向 */
        ValueAnimator yValueAnimator = ValueAnimator.ofFloat(mFooterCircle.y, mY + mHeight / 2);
        yValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //动画执行的同时不断调用，不断刷新视图
                mFooterCircle.y = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        /* 用户添加的视图x、y方向 */
        ObjectAnimator objectAnimator = null;
        if (mView != null) {
            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("X", mFooterCircle.x - mWidth / 2, mX);
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("Y", mFooterCircle.y - mHeight / 2, mY);
            objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mView, pvhX, pvhY);
        }
         
        /* 动画集合 */
        AnimatorSet animSet = new AnimatorSet();
        if (mView != null)
            animSet.play(xValueAnimator).with(yValueAnimator).with(objectAnimator);
        else
            animSet.play(xValueAnimator).with(yValueAnimator);
        animSet.setInterpolator(new BounceInterpolator());
        animSet.setDuration(400);
        animSet.start();
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //动画结束的时候，重置所有参数
                reset();
            }
        });

    }

    /**
     * 设置宽和高
     *
     * @param width
     * @param height
     */
    private void setWidthAndHeight(int width, int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);

    }

    /**
     * 对外接口：设置颜色
     *
     * @param color
     */
    public void setColor(int color) {
        mColor = color;
        mPaint.setColor(mColor);
    }

    /**
     * 对外接口：设置是否可以扯断
     */
    public void setSnapEnable(boolean isSnap) {
        this.isSnap = isSnap;
    }

    /**
     * 对外接口：设置粘连的最大长度
     *
     * @param maxAdherentLength
     */
    public void setMaxAdherentLength(int maxAdherentLength) {
        mMaxAdherentLength = maxAdherentLength;
    }

    /**
     * 对外接口：设置头部的最小半径
     *
     * @param minHeaderCircleRadius
     */
    public void setMinHeaderCircleRadius(int minHeaderCircleRadius) {
        mMinHeaderCircleRadius = minHeaderCircleRadius;
    }

    /**
     * 对外接口：设置监听器
     *
     * @param onAdherentListener
     */
    public void setOnAdherentListener(OnAdherentListener onAdherentListener) {
        mOnAdherentListener = onAdherentListener;
    }

    /**
     * 圆类
     */
    private class Circle {
        public float x;
        public float y;
        public float radius;
    }

    /**
     * 监听器
     */
    public interface OnAdherentListener {
        public void onDismiss();
    }
}
