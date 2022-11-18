/*
 * Copyright (C) 2018 Beijing GAGO Technology Ltd.
 */
package com.demo.arcgis.util

import android.content.res.Resources

/**
 * @author guoyalong
 * @desc
 */
object DensityUtil {
    /**
     * dip to px
     *
     * @param dpValue value
     * @return px
     */
    fun dip2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px to dip
     *
     * @param pxValue value
     * @return dip
     */
    fun px2dip(pxValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}