package com.demo.mapbox.util

import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.UiSettings


/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
 */
object MapViewUtil {
    /**
     * 隐藏logo
     *
     * @param mapboxMap mapboxMap
     */
    fun hideLogo(mapboxMap: MapboxMap) {
        val uiSettings: UiSettings = mapboxMap.uiSettings
        uiSettings.isCompassEnabled = false // 隐藏指南针
        uiSettings.isLogoEnabled = false // 隐藏logo
        uiSettings.isRotateGesturesEnabled = false // 设置是否可以旋转地图
        uiSettings.isAttributionEnabled = false // 设置隐藏显示那个提示按钮
        uiSettings.isTiltGesturesEnabled = false // 设置是否可以调整地图倾斜角
    }
}