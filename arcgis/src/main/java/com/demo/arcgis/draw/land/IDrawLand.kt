package com.demo.arcgis.draw.land

import android.graphics.Point

/**
 * @author guoyalong
 * @time 2022/11/03
 * @desc 地块操作的一些接口
 */
interface IDrawLand {
    /**
     * 添加点
     *
     * @param point 点
     */
    fun add(point: com.esri.arcgisruntime.geometry.Point?)

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
    fun selectLand(point: com.esri.arcgisruntime.geometry.Point?): Boolean

    /**
     * 点击地图是否点中了某个点
     *
     * @param point 点击位置
     * @return 是否点中
     */
    fun touchPoint(point: Point): Boolean
}