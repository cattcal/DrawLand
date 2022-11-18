package com.demo.arcgis.draw.land

import com.esri.arcgisruntime.geometry.*

/**
 * @author guoyalong
 * @time 2022/11/03
 * @desc 获取接口
 */
interface IResultData {
    /**
     * 获取 geometry 信息
     *
     * @return geometry 信息
     */
    val geometry: Geometry?

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
    val pointList: List<Point?>
}