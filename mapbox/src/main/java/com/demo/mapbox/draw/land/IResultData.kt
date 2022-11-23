package com.demo.mapbox.draw.land

import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
 */
interface IResultData {
    /**
     * 获取面积
     *
     * @return 面积
     */
    val area: String?

    /**
     * 获取点集合
     *
     * @return 点集合
     */
    val pointList: List<LatLng?>
}