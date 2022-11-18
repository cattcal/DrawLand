package com.demo.arcgis.util

import com.esri.arcgisruntime.geometry.Geometry
import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.AreaUnit
import com.esri.arcgisruntime.geometry.AreaUnitId
import com.esri.arcgisruntime.geometry.GeodeticCurveType
import com.esri.arcgisruntime.geometry.LinearUnit
import com.esri.arcgisruntime.geometry.LinearUnitId
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * @author guoyalong
 * @desc arcgis 空间信息工具类
 */
object GeometryUtil {
    /**
     * 获取面积
     *
     * @param geometry 空间信息
     * @return 面积
     */
    fun getArea(geometry: Geometry?): BigDecimal {
        val area: Double
        area = GeometryEngine.areaGeodetic(
            geometry, AreaUnit(AreaUnitId.SQUARE_METERS),
            GeodeticCurveType.NORMAL_SECTION
        )
        val decimalFormat = DecimalFormat("#.00")
        val format = decimalFormat.format(area * 0.0015)
        return BigDecimal(format)
    }

    /**
     * 获取周长
     *
     * @param geometry 空间信息
     * @return 长度
     */
    fun getLength(geometry: Geometry?): BigDecimal {
        val length: Double
        length = GeometryEngine.lengthGeodetic(
            geometry, LinearUnit(LinearUnitId.METERS),
            GeodeticCurveType.NORMAL_SECTION
        )
        val decimalFormat = DecimalFormat("#.00")
        val format = decimalFormat.format(length)
        return BigDecimal(format)
    }
}