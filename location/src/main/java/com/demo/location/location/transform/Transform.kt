package com.demo.location.location.transform

import com.amap.api.location.DPoint

/**
 * @author guoyalong
 * @time 2022/11/08
 * @desc 坐标转换
 */
interface Transform {
    /**
     * 坐标转化
     *
     * @param value 转化值
     * @return 返回转换值
     */
    fun transform(value: DPoint?): DPoint
}