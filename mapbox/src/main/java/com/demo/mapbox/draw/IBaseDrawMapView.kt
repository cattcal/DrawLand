package com.demo.mapbox.draw

import android.os.Bundle
import com.mapbox.mapboxsdk.geometry.LatLng
import com.demo.mapbox.draw.manager.DrawLandManager
import com.demo.mapbox.draw.manager.DrawType
import com.demo.mapbox.mapview.BaseMapView

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc: 地块勾画接口
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
    fun changeImageLayer(s: String)

    /**
     * 添加点
     *
     * @param point 点
     */
    fun addPoint(point: LatLng?)

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
     * onCreate
     *
     * @param savedInstanceState savedInstanceState
     */
    fun onCreate(savedInstanceState: Bundle?)

    /**
     * onStart
     */
    fun onStart()

    /**
     * onResume
     */
    fun onResume()

    /**
     * onPause
     */
    fun onPause()

    /**
     * onStop
     */
    fun onStop()

    /**
     * onSaveInstanceState
     *
     * @param outState outState
     */
    fun onSaveInstanceState(outState: Bundle?)

    /**
     * onLowMemory
     */
    fun onLowMemory()

    /**
     * onDestroy
     */
    fun onDestroy()
}