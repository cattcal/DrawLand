package com.demo.mapbox.util

import android.content.res.Resources

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
 */
object DensityUtil {
    /**
     * dip to px
     *
     * @param dpValue value
     * @return px
     */
    @JvmStatic
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