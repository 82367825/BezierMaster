package com.zero.bezier.widget.elastic;

/**
 * 球类
 * @author linzewu
 * @date 2016/6/1
 */
public class Ball {
    /**
     * 圆心横坐标
     */
    public float x;
    /**
     * 圆心纵坐标
     */
    public float y;
    /**
     * 半径
     */
    public float radius;

    /**
     * 构造方法
     * @param x
     * @param y
     * @param radius
     */
    public Ball(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.topX = x;
        this.topY = y - radius;
        this.bottomX = x;
        this.bottomY = y + radius;
        this.leftX = x - radius;
        this.leftY = y;
        this.rightX = x + radius;
        this.rightY = y;
    }
    
    public void refresh(float x, float y, float topX, float topY, float bottomX, float bottomY,
                        float leftX, float leftY, float rightX, float rightY){
        this.x = x;
        this.y = y;
        this.topX = topX;
        this.topY = topY;
        this.bottomX = bottomX;
        this.bottomY = bottomY;
        this.leftX = leftX;
        this.leftY = leftY;
        this.rightX = rightX;
        this.rightY = rightY;
    }
    
    /**
     * 球左边点的坐标
     */
    public float leftX;
    public float leftY;

    /**
     * 球右边点的坐标
     */
    public float rightX;
    public float rightY;

    /**
     * 球顶点的坐标
     */
    public float topX;
    public float topY;

    /**
     * 球底部点的坐标
     */
    public float bottomX;
    public float bottomY;
}
