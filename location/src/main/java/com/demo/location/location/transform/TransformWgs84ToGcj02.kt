package com.demo.location.location.transform

import com.demo.location.location.util.CoordinateTransformUtil
import com.amap.api.location.DPoint

/**
 * @author guoyalong
 * @time 2022/11/08
 * @desc 坐标转换将wgs 84坐标转成 国测局02坐标
 */
class TransformWgs84ToGcj02 : Transform {
    override fun transform(value: DPoint?): DPoint {
        val result = DPoint()
        val point = CoordinateTransformUtil.transformLocationFromWgs84ToGcj02(
            value!!.latitude, value.longitude
        )
        result.latitude = point!![0]
        result.longitude = point[1]
        return result
    }
}