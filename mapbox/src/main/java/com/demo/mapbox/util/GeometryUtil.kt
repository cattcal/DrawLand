package com.demo.mapbox.util

import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.turf.TurfAssertions
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfJoins
import com.mapbox.turf.TurfMeasurement
import java.math.BigDecimal
import java.text.DecimalFormat
/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
 */
object GeometryUtil {
    /**
     * 获取面积
     *
     * @param latLngList 点集合
     * @return 面积
     */
    @JvmStatic
    fun getArea(latLngList: List<LatLng>): BigDecimal {
        val points: MutableList<List<Point>> = ArrayList()
        val points1: MutableList<Point> = ArrayList()
        for (latLng in latLngList) {
            val point = Point.fromLngLat(latLng.longitude, latLng.latitude)
            points1.add(point)
        }
        points.add(points1)
        val polygon = Polygon.fromLngLats(points)
        val area = TurfMeasurement.area(polygon)
        val decimalFormat = DecimalFormat("#.00")
        val format = decimalFormat.format(area * 0.0015)
        return BigDecimal(format)
    }

    /**
     * 获取中心点
     *
     * @param latLngList 点集合
     * @return 中心点
     */
    @JvmStatic
    fun getCenter(latLngList: List<LatLng>): LatLng {
        val points: MutableList<Point> = ArrayList()
        for (latLng in latLngList) {
            val point = Point.fromLngLat(latLng.longitude, latLng.latitude)
            points.add(point)
        }
        val lineString = LineString.fromLngLats(points)
        val center = TurfMeasurement.center(Feature.fromGeometry(lineString))
        val coord = TurfAssertions.getCoord(center)
        return LatLng(coord.latitude(), coord.longitude())
    }

    /**
     * 获取周长
     *
     * @param latLngList 点集合
     * @return 长度
     */
    @JvmStatic
    fun getLength(latLngList: List<LatLng>): BigDecimal {
        val points1: MutableList<Point> = ArrayList()
        for (latLng in latLngList) {
            val point = Point.fromLngLat(latLng.longitude, latLng.latitude)
            points1.add(point)
        }
        val lineString = LineString.fromLngLats(points1)
        val length = TurfMeasurement.length(lineString, TurfConstants.UNIT_METERS)
        val decimalFormat = DecimalFormat("#.00")
        val format = decimalFormat.format(length)
        return BigDecimal(format)
    }

    /**
     * 是否选中某个区域
     *
     * @param point      点
     * @param latLngList 面
     * @return 点是否在面内
     */
    @JvmStatic
    fun isSelect(point: LatLng, latLngList: List<LatLng>): Boolean {
        val pointLists: MutableList<List<Point>> = ArrayList()
        val points: MutableList<Point> = ArrayList()
        for (latLng in latLngList) {
            points.add(Point.fromLngLat(latLng.longitude, latLng.latitude))
        }
        pointLists.add(points)
        val polygon = Polygon.fromLngLats(pointLists)
        return TurfJoins.inside(Point.fromLngLat(point.longitude, point.latitude), polygon)
    }
}