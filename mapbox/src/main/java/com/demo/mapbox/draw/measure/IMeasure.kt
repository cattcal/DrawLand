package com.demo.mapbox.draw.measure

import android.graphics.PointF
import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
 */
interface IMeasure {
    /**
     * 添加点
     *
     * @param latLng 点
     */
    fun add(latLng: LatLng?)

    /**
     * 删除，清空操作
     */
    fun delete()

    /**
     * 撤销到上部操作
     */
    fun undo()

    /**
     * 是否可以撤销
     */
    val isUndo: Boolean

    /**
     * 地块完成
     */
    fun complete()

    /**
     * 点击地图是否点中了某个点
     *
     * @param point 点击位置
     * @return 是否点中
     */
    fun touchPoint(point: PointF): Boolean
}