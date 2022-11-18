package com.demo.arcgis.draw

import com.demo.arcgis.draw.manager.DrawType
import com.demo.arcgis.mapview.BaseMapView
import com.demo.arcgis.draw.manager.DrawLandManager
import com.esri.arcgisruntime.geometry.*

/**
 * @author guoyalong
 * @time 2022/11/09
 * @desc 地块勾画接口
 */
interface IBaseDrawMapView {
    /**
     * 获取baseMapView
     *
     * @return baseMapView
     */
    val baseMapView: BaseMapView?

    /**
     * 获取勾画管理类
     *
     * @return 勾画管理类
     */
    val drawLandManager: DrawLandManager?

    /**
     * 修改底图
     */
    fun changeImageLayer()

    /**
     * 添加点
     *
     * @param point 点
     */
    fun addPoint(point: Point?)

    /**
     * 删除操作
     */
    fun delete()

    /**
     * 撤销操作
     */
    fun undo()

    /**
     * 完成操作
     */
    fun complete()

    /**
     * 开始勾画
     *
     * @param drawType 勾画类型
     */
    fun startDraw(drawType: DrawType?)

    /**
     * onPause
     */
    fun onPause()

    /**
     * onResume
     */
    fun onResume()

    /**
     * onDestroy
     */
    fun onDestroy()
}