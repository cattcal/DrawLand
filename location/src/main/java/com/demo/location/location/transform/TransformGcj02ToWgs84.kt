package com.demo.location.location.transform

import com.demo.location.location.util.CoordinateTransformUtil
import com.amap.api.location.DPoint

/**
 * @author guoyalong
 * @time 2022/11/08
 * @desc 坐标转换将 国测局02坐标转成wgs 84坐标
 */
class TransformGcj02ToWgs84 : Transform {
    override fun transform(value: DPoint?): DPoint {
        val result = DPoint()
        val point = CoordinateTransformUtil.transformLocationFromGcj02ToWgs84(
            value!!.latitude, value.longitude
        )
        result.latitude = point!![0]
        result.longitude = point[1]
        return result
    }
}