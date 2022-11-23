package com.demo.mapbox.draw.land

import android.graphics.Color
import android.graphics.PointF
import com.demo.mapbox.draw.entity.LandEntity
import com.demo.mapbox.util.DensityUtil
import com.demo.mapbox.util.GeometryUtil.getArea
import com.demo.mapbox.util.GeometryUtil.getCenter
import com.demo.mapbox.util.GeometryUtil.isSelect
import com.demo.mapbox.util.NumberFormatUtil.doubleRemoveEndZero
import com.google.gson.JsonObject
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import java.util.Stack

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
 */
class DrawLand(private val mMap: MapboxMap?, uuids: String) : IDrawLand, IResultData {


    private val mLandOperas: Stack<LandEntity> = Stack()
    private val mUuid: String
    var selectIndex = 0
    var mPointList: MutableList<LatLng?> = mutableListOf()
    override var area: String? = null

    private var state = -1 // 地块状态 -1代表新增 1代表修改
    private var mLandEntity // 地块详情
            : com.demo.drawland.LandEntity? = null
    constructor(mMap: MapboxMap?, uuids: String, entity: com.demo.drawland.LandEntity?, state: Int) : this(mMap, uuids){
        this.mLandEntity=entity
        mPointList.addAll(mLandEntity?.latLngs!!)
        this.state=state
        draw()
    }
    init {
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
//        drawPolygon()
//        drawLine()
//        drawArea()
    }

    override fun selectLand(latLng: LatLng): Boolean {
        val isSelectLand = isSelect(latLng, mPointList)
        if (isSelectLand) {
            draw()
        }
        return isSelectLand
    }

    override fun touchPoint(point: PointF): Boolean {
        for (i in mPointList.indices) {
            val point1 = mPointList[i]
            val point2 = mMap!!.projection.toScreenLocation(point1!!)
            if (Math.abs(point.x - point2.x) < DensityUtil.dip2px(20f) && Math.abs(point.y - point2.y) < DensityUtil.dip2px(
                    20f
                )
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
     * 开始move
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

    private fun draw() {
        clear()
        drawPolygon()
        drawLine()
        drawPoint()
        drawArea()
    }

    override val pointList: MutableList<LatLng?>
        get() = mPointList

    /**
     * 画面
     */
    private fun drawPolygon() {
        if (mPointList.size < 3) {
            return
        }
        val pointss: MutableList<List<Point>> = ArrayList()
        val points: MutableList<Point> = ArrayList()
        for (latLng in mPointList) {
            points.add(Point.fromLngLat(latLng!!.longitude, latLng.latitude))
        }
        pointss.add(points)
        val polygon = Polygon.fromLngLats(pointss)
        val feature = Feature.fromGeometry(polygon)
        val jsonSource = GeoJsonSource(POLYGON_SOURCE + mUuid, feature)
        mMap!!.style!!.addSource(jsonSource)
        val fillLayer = FillLayer(POLYGON_LAYER + mUuid, POLYGON_SOURCE + mUuid)
        fillLayer.setProperties(
            PropertyFactory.fillColor(Color.WHITE), PropertyFactory.fillOpacity(0.4f)
        )
        mMap.style!!.addLayer(fillLayer)
    }

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
        val positions: MutableList<Point> = ArrayList(points)
        if (points.size > 2) {
            positions.add(points[0])
        }
        val lineString = LineString.fromLngLats(positions)
        features.add(Feature.fromGeometry(lineString))
        val featureCollection = FeatureCollection.fromFeatures(features)
        val jsonSource = GeoJsonSource(LINE_SOURCE + mUuid, featureCollection)
        mMap!!.style!!.addSource(jsonSource)
        val lineLayer = LineLayer(LINE_LAYER + mUuid, LINE_SOURCE + mUuid)
        lineLayer.setProperties(
            PropertyFactory.lineColor(Color.YELLOW), PropertyFactory.lineWidth(3f)
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
        val circleLayer = CircleLayer(POINT_LAYER + mUuid, POINT_SOURCE + mUuid)
        val stops = arrayOfNulls<Expression.Stop>(mPointList.size)
        for (i in mPointList.indices) {
            if (i == selectIndex - 1) {
                stops[i] = Expression.stop(i, Expression.rgb(255, 0, 0))
            } else {
                stops[i] = Expression.stop(i, Expression.rgb(255, 255, 255))
            }
        }
        // 进行筛选，只有符合下标符合的才显示出来
//        circleLayer.setFilter(Expression.eq(Expression.get("index"), mSelectIndex - 1));
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

    private fun drawArea() {
        if (mPointList.size < 3) {
            return
        }
        area = getArea(mPointList).toEngineeringString()
        val center = getCenter(mPointList)
        val point = Point.fromLngLat(center!!.longitude, center.latitude)
        val jsonObject = JsonObject()
        jsonObject.addProperty("area", doubleRemoveEndZero(area.toString()) + "亩")
        val jsonSource = GeoJsonSource(AREA_SOURCE + mUuid, Feature.fromGeometry(point, jsonObject))
        mMap!!.style!!.addSource(jsonSource)
        val symbolLayer = SymbolLayer(AREA_LAYER + mUuid, AREA_SOURCE + mUuid)
        symbolLayer.setProperties(
            PropertyFactory.textColor(Color.BLACK),
            PropertyFactory.textField("{area}"),
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
        deletePolygon()
        deleteArea()
    }

    /**
     * 删除点
     */
    private fun deletePoint() {
        mMap?.style?.apply {
            removeLayer(POINT_LAYER + mUuid)
            removeSource(POINT_SOURCE + mUuid)
        }

    }

    /**
     * 删除线
     */
    private fun deleteLine() {
        mMap?.style?.apply {
            removeLayer(LINE_LAYER + mUuid)
            removeSource(LINE_SOURCE + mUuid)
        }

    }

    /**
     * 删除面
     */
    private fun deletePolygon() {
        mMap?.style?.apply {
            removeLayer(POLYGON_LAYER + mUuid)
            removeSource(POLYGON_SOURCE + mUuid)
        }

    }

    /**
     * 删除面积
     */
    private fun deleteArea() {
        mMap?.style?.apply {
            removeLayer(AREA_LAYER + mUuid)
            removeSource(AREA_SOURCE + mUuid)
        }

    }

    companion object {
        // 点资源
        private const val POINT_SOURCE = "point_source"
        private const val POINT_LAYER = "point_layer"

        // 线资源
        private const val LINE_SOURCE = "line_source"
        private const val LINE_LAYER = "line_layer"

        // 面资源
        private const val POLYGON_SOURCE = "polygon_source"
        private const val POLYGON_LAYER = "polygon_layer"

        // 面积资源
        private const val AREA_SOURCE = "area_source"
        private const val AREA_LAYER = "area_layer"
    }
}