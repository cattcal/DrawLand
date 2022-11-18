package com.demo.arcgis.mapview

import com.demo.location.location.ILocation

/**
 * @author guoyalong
 * @time 2022/11/08
 * @desc 定位listener
 */
interface ILocationListener {
    /**
     * 定位listener
     *
     * @param location 位置
     */
    fun locationListener(location: ILocation?)
}