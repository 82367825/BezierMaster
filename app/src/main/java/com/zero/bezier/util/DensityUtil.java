package com.zero.bezier.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

/**
 * @author 
 * @version 1.0
 * @date 16-2-2
 */
public class DensityUtil {

    public static float sDensity = 1.0f;
    public static int sDensityDpi;
    public static int sWidthPixels = 0;
    public static int sHeightPixels = 0;
    public static float sTouchSlop = 15f;
    public static float sFontDensity;
    public static int sTopStatusHeight;

    public static void resetDensity(Context context) {
        if (context != null && null != context.getResources()) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            sDensity = metrics.density;
            sFontDensity = metrics.scaledDensity;
            sWidthPixels = metrics.widthPixels;
            sHeightPixels = metrics.heightPixels;
            sDensityDpi = metrics.densityDpi;
            try {
                final ViewConfiguration configuration = ViewConfiguration.get(context);
                if (null != configuration) {
                    sTouchSlop = configuration.getScaledTouchSlop();
                }
            } catch (Error e) {
                Log.i("DensityUtils", "resetDensity has error" + e.getMessage());
            }
        }
    }

    public static int getStatusBarHeight(Context context)
    {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        int top = 0;
        try
        {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            top = context.getResources().getDimensionPixelSize(x);
            sTopStatusHeight = top;
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        return top;
    }

    /**
     * dip/dp转像素
     *
     * @param dipValue
     *            dip或 dp大小
     * @return 像素值
     */
    public static int dip2px(float dipValue) {
        return (int) (dipValue * sDensity + 0.5f);
    }

    /**
     * 像素转dip/dp
     *
     * @param pxValue
     *            像素大小
     * @return dip值
     */
    public static int px2dip(float pxValue) {
        final float scale = sDensity;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp 转 px
     *
     * @param spValue
     *            sp大小
     * @return 像素值
     */
    public static int sp2px(float spValue) {
        final float scale = sDensity;
        return (int) (scale * spValue);
    }

    /**
     * px转sp
     *
     * @param pxValue
     *            像素大小
     * @return sp值
     */
    public static int px2sp(float pxValue) {
        final float scale = sDensity;
        return (int) (pxValue / scale);
    }
}
