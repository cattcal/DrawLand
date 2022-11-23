package com.demo.mapbox.draw.measure

import android.graphics.Color
import android.graphics.PointF
import com.google.gson.JsonObject
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.demo.mapbox.draw.entity.LandEntity
import com.demo.mapbox.util.DensityUtil
import com.demo.mapbox.util.DensityUtil.dip2px
import com.demo.mapbox.util.GeometryUtil
import com.demo.mapbox.util.GeometryUtil.getCenter
import com.demo.mapbox.util.GeometryUtil.getLength
import com.demo.mapbox.util.NumberFormatUtil
import com.demo.mapbox.util.NumberFormatUtil.doubleRemoveEndZero
import java.util.Stack

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc: 测量长度
 */
class MeasureLength(private val mMap: MapboxMap?, uuids: String) : IMeasure {
    private val mLandOperas: Stack<LandEntity>
    private val mUuid: String

    /**
     * 获取选中的点
     *
     * @return 选中的点
     */
    var selectIndex = 0
        private set
    private val mPointList: MutableList<LatLng?>

    init {
        mLandOperas = Stack()
        mUuid = uuids
        mPointList = ArrayList()
    }

    override fun add(point: LatLng?) {
        val pointF = mMap!!.projection.toScreenLocation(point!!)
        if (!touchPoint(pointF)) {
            mLandOperas.push(LandEntity(mPointList.size, mPointList))
            mPointList.add(point)
            selectIndex = mPointList.size
            draw()
        }
    }

    override fun delete() {
        mPointList.clear()
        mLandOperas.clear()
        clear()
    }

    override fun undo() {
        if (isUndo) {
            val pop = mLandOperas.pop()
            mPointList.clear()
            mPointList.addAll(pop.latLngList)
            selectIndex = pop.selectPosition
            draw()
        }
    }

    override val isUndo: Boolean
        get() = !mLandOperas.isEmpty()

    override fun complete() {
        clear()
        drawLine()
        drawLength()
    }

    override fun touchPoint(point: PointF): Boolean {
        for (i in mPointList.indices) {
            val point1 = mPointList[i]
            val point2 = mMap!!.projection.toScreenLocation(point1!!)
            if (Math.abs(point.x - point2.x) < DensityUtil.dip2px(20f)
                && Math.abs(point.y - point2.y) < DensityUtil.dip2px(20f)
            ) {
                if (i != selectIndex - 1) { // 如果不是选中的
                    selectIndex = i + 1
                    draw()
                }
                return true
            }
        }
        return false
    }

    /**
     * 开始移动
     */
    val isStartMove: Unit
        get() {
            mLandOperas.push(LandEntity(mPointList.size, mPointList))
            clear()
        }

    /**
     * 移动过程中
     */
    fun moveLand() {}

    /**
     * 移动结束
     *
     * @param point 最后的坐标点
     */
    fun moveEnd(point: LatLng?) {
        mPointList.removeAt(selectIndex - 1)
        mPointList.add(selectIndex - 1, point)
        draw()
    }

    /**
     * 绘制
     */
    private fun draw() {
        clear()
        drawLine()
        drawPoint()
        drawLength()
    }

    /**
     * 获取点集合
     *
     * @return 点集合
     */
    val pointList: List<LatLng?>
        get() = mPointList

    /**
     * 画线
     */
    private fun drawLine() {
        if (mPointList.size < 2) {
            return
        }
        val points: MutableList<Point> = ArrayList()
        for (latLng in mPointList) {
            points.add(Point.fromLngLat(latLng!!.longitude, latLng.latitude))
        }
        val features: MutableList<Feature> = ArrayList()
        val positions: List<Point> = ArrayList(points)
        val lineString = LineString.fromLngLats(positions)
        features.add(Feature.fromGeometry(lineString))
        val featureCollection = FeatureCollection.fromFeatures(features)
        val jsonSource = GeoJsonSource(LINE_SOURCE + mUuid, featureCollection)
        mMap!!.style!!.addSource(jsonSource)
        val lineLayer = LineLayer(LINE_LAYER + mUuid, LINE_SOURCE + mUuid)
        lineLayer.setProperties(
            PropertyFactory.lineColor(Color.YELLOW),
            PropertyFactory.lineWidth(3f)
        )
        mMap.style!!.addLayer(lineLayer)
    }

    /**
     * 画点
     */
    private fun drawPoint() {
        val features: MutableList<Feature> = ArrayList(mPointList.size)
        val points: MutableList<Point> = ArrayList()
        for (latLng in mPointList) {
            points.add(Point.fromLngLat(latLng!!.longitude, latLng.latitude))
        }
        for (i in points.indices) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("index", i)
            features.add(Feature.fromGeometry(points[i], jsonObject))
        }
        val featureCollection = FeatureCollection.fromFeatures(features)
        val jsonSource = GeoJsonSource(POINT_SOURCE + mUuid, featureCollection)
        mMap!!.style!!.addSource(jsonSource)
        val stops = arrayOfNulls<Expression.Stop>(mPointList.size)
        for (i in mPointList.indices) {
            if (i == selectIndex - 1) {
                stops[i] = Expression.stop(i, Expression.rgb(255, 0, 0))
            } else {
                stops[i] = Expression.stop(i, Expression.rgb(255, 255, 255))
            }
        }
        val circleLayer = CircleLayer(POINT_LAYER + mUuid, POINT_SOURCE + mUuid)
        circleLayer.setProperties(
            PropertyFactory.circleColor(
                Expression.interpolate(Expression.linear(), Expression.get("index"), *stops)
            ),
            PropertyFactory.circleRadius(8f),
            PropertyFactory.circleStrokeWidth(4f),
            PropertyFactory.circleStrokeColor(Color.WHITE)
        )
        mMap.style!!.addLayer(circleLayer)
    }

    private fun drawLength() {
        if (mPointList.size < 2) {
            return
        }
        val length = getLength(mPointList).toEngineeringString()
        val center = getCenter(mPointList)
        val point = Point.fromLngLat(center.longitude, center.latitude)
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "length",
            doubleRemoveEndZero(length.toString()) + "米"
        )
        val jsonSource =
            GeoJsonSource(LENGTH_SOURCE + mUuid, Feature.fromGeometry(point, jsonObject))
        mMap!!.style!!.addSource(jsonSource)
        val symbolLayer = SymbolLayer(LENGTH_LAYER + mUuid, LENGTH_SOURCE + mUuid)
        symbolLayer.setProperties(
            PropertyFactory.textColor(Color.BLACK),
            PropertyFactory.textField("{length}"),
            PropertyFactory.textSize(16f),
            PropertyFactory.textHaloWidth(2f),
            PropertyFactory.textHaloColor(Color.WHITE)
        )
        mMap.style!!.addLayer(symbolLayer)
    }

    /**
     * 清空资源
     */
    private fun clear() {
        deletePoint()
        deleteLine()
        deleteArea()
    }

    /**
     * 删除点
     */
    private fun deletePoint() {
        mMap!!.style!!.removeLayer(POINT_LAYER + mUuid)
        mMap.style!!.removeSource(POINT_SOURCE + mUuid)
    }

    /**
     * 删除线
     */
    private fun deleteLine() {
        mMap!!.style!!.removeLayer(LINE_LAYER + mUuid)
        mMap.style!!.removeSource(LINE_SOURCE + mUuid)
    }

    /**
     * 删除长度
     */
    private fun deleteArea() {
        mMap!!.style!!.removeLayer(LENGTH_LAYER + mUuid)
        mMap.style!!.removeSource(LENGTH_SOURCE + mUuid)
    }

    companion object {
        // 点资源
        private const val POINT_SOURCE = "point_source"
        private const val POINT_LAYER = "point_layer"

        // 线资源
        private const val LINE_SOURCE = "line_source"
        private const val LINE_LAYER = "line_layer"

        // 长度
        private const val LENGTH_SOURCE = "length_source"
        private const val LENGTH_LAYER = "length_layer"
    }
}