package com.demo.mapbox.draw.land

import android.graphics.PointF
import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
 */
interface IDrawLand {
    /**
     * 添加点
     *
     * @param point 点
     */
    fun add(point: LatLng?)

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
     *
     * @return 是否可以撤销
     */
    val isUndo: Boolean

    /**
     * 地块完成
     */
    fun complete()

    /**
     * 点击地图地块是否被选中
     *
     * @param point 点击位置
     * @return 是否被选中
     */
    fun selectLand(point: LatLng): Boolean

    /**
     * 点击地图是否点中了某个点
     *
     * @param point 点击位置
     * @return 是否点中
     */
    fun touchPoint(point: PointF): Boolean
}