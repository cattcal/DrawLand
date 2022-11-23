package com.demo.mapbox.draw

import com.mapbox.mapboxsdk.maps.MapboxMap

/**
 * @author: hujw
 * @time: 2022/11/22
 * @desc:
 */
interface LoadDrawMapViewListener {
    /**
     * 加载成功
     *
     * @param mapboxMap mapboxMap
     */
    fun loadSuccess(mapboxMap: MapboxMap?)
}