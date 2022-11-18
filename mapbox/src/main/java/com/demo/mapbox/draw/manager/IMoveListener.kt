package com.demo.mapbox.draw.manager

import android.graphics.PointF
import android.view.MotionEvent

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
 */
interface IMoveListener {
    /**
     * 开始移动事件
     *
     * @param event       event
     * @param points      屏幕的坐标点
     * @param selectIndex 移动点的下标
     */
    fun startMove(event: MotionEvent?, points: MutableList<PointF>?, selectIndex: Int)

    /**
     * 触摸事件
     *
     * @param event event
     */
    fun onTouchEvent(event: MotionEvent?)

    /**
     * 地图长按事件
     */
    fun onMapLongClicked(isSelect: Boolean)

    /**
     * 地图缩放结束
     */
    fun onScaleEnd()

    /**
     * 抬手操作
     *
     * @param isSelect 是否选中
     * @param event    事件
     */
    fun onSingleTapUp(isSelect: Boolean, event: MotionEvent?)
}