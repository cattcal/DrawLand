package com.demo.arcgis

import com.esri.arcgisruntime.geometry.SpatialReference

/**
 * @author guoyalong
 * @time 2022/11/03
 * @desc 一些常用的属性字段
 */
object Contracts {
    /**
     * 跟坐标系相关
     */
    val CUSTOM_REFERENCE = SpatialReference.create(4490)
}