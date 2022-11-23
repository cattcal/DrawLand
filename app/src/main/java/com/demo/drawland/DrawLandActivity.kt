package com.demo.drawland

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.demo.base.BaseDialog
import com.demo.drawland.aop.SingleClick
import com.demo.drawland.app.AppActivity
import com.demo.drawland.ui.dialog.MessageDialog
import com.demo.mapbox.draw.BaseDrawMapView
import com.demo.mapbox.draw.LoadDrawMapViewListener
import com.demo.mapbox.draw.manager.DrawType
import com.demo.mapbox.mapview.BaseMapView
import com.demo.mapbox.util.AssetsUtil
import com.demo.mapbox.util.NumberFormatUtil
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.hjq.gson.factory.GsonFactory
import com.hjq.shape.view.ShapeImageView
import com.hjq.shape.view.ShapeTextView
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import java.lang.String
import kotlin.Int
import kotlin.apply
import kotlin.getValue
import kotlin.lazy

/**
 * @author: hujw
 * @time: 2022/11/21
 * @desc: 绘制地块
 */
class DrawLandActivity : AppActivity() {

    private var mMapboxMap: MapboxMap? = null

    private val mBaseDrawMapView: BaseDrawMapView? by lazy { findViewById(R.id.base_draw_map_view) }
    private var mBaseMapView: BaseMapView? = null
    private val mLocationView: ShapeImageView? by lazy { findViewById(R.id.iv_location) }

    private val mStartDrawLandView: ShapeTextView? by lazy { findViewById(R.id.stv_start_draw_land) }
    private val mSaveView: ShapeTextView? by lazy { findViewById(R.id.stv_save) }
    private val mUndoView: ShapeTextView? by lazy { findViewById(R.id.stv_undo) }


    // 已有的地块
    private val mAlreadyLands: MutableList<LandEntity> = ArrayList()

    // 删除的地块
    private val mDeleteLands: MutableList<LandEntity> = ArrayList()

    // 第一次加载
    private var isFirstLoad = true

    private val isShowSelfLand = true
    private val isShowLatLng = false
    private val isShowFourMap = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBaseDrawMapView?.onCreate(savedInstanceState)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_draw_land_for_mapbox
    }

    override fun initView() {
        mBaseDrawMapView?.initDrawMapView()
        mBaseMapView = mBaseDrawMapView?.baseMapView

        setOnClickListener(mLocationView, mStartDrawLandView, mSaveView, mUndoView)
    }

    @SingleClick
    override fun onClick(view: View) {
        when (view) {
            mLocationView -> {
                mBaseMapView?.startLocation()
            }

            mUndoView -> {
                mBaseDrawMapView?.undo()
            }

            mStartDrawLandView -> {
                if (mStartDrawLandView?.text?.equals("开始圈地")!!) {
                    mBaseDrawMapView?.startDraw(DrawType.DRAW_LAND)
                    mStartDrawLandView?.text = "退出圈地"
                    mSaveView?.visibility = View.VISIBLE
                    mUndoView?.visibility = View.VISIBLE
                } else {
                    mBaseDrawMapView?.delete()
                    mStartDrawLandView?.text = "开始圈地"
                    mSaveView?.visibility = View.INVISIBLE
                    mUndoView?.visibility = View.INVISIBLE
                }
            }

            mSaveView -> {
                val landEntity = LandEntity()
                landEntity.id = mAlreadyLands.size.toString()
                val land = mBaseDrawMapView?.drawLandManager?.editDrawLand
                landEntity.latLngs = land?.pointList
                landEntity.area = land?.area?.toDouble()!!
                landEntity.userName = "李四"
                val latLngBounds = LatLngBounds.Builder().includes(landEntity.latLngs!!).build()
                landEntity.center = latLngBounds.center
                val newPath = mutableListOf<LandEntity.PathBean>()

                land.pointList.forEach {
                    val bean = LandEntity.PathBean()
                    bean.longitude = it!!.longitude
                    bean.latitude = it.latitude
                    newPath.add(bean)
                }
                landEntity.path = newPath

                mAlreadyLands.add(landEntity)
                mBaseDrawMapView?.complete()
                showAllLand()
                mStartDrawLandView?.text = "开始圈地"
                mSaveView?.visibility = View.INVISIBLE
                mUndoView?.visibility = View.INVISIBLE
            }
        }
    }

    override fun initData() {
        val landData = AssetsUtil.loadStringFromAssets(this@DrawLandActivity, "land.json")

        val landEntityList: MutableList<LandEntity> = GsonFactory.getSingletonGson()
            .fromJson(landData, object : TypeToken<MutableList<LandEntity>>() {}.type)
        mAlreadyLands.addAll(landEntityList)
        mAlreadyLands.forEach {
            it.state = 0
            val paths: List<LandEntity.PathBean>? = it.path
            val latLngs: MutableList<LatLng?> = ArrayList()
            if (paths != null) {
                for (path in paths) {
                    val latLng = LatLng(path.latitude, path.longitude)
                    latLngs.add(latLng)
                }
            }
            val latLngBounds = LatLngBounds.Builder().includes(latLngs).build()
            it.center = latLngBounds.center
            it.latLngs = latLngs
        }

        mBaseDrawMapView?.setOnLoadDrawMapViewListener(object : LoadDrawMapViewListener {
            override fun loadSuccess(mapboxMap: MapboxMap?) {
                mMapboxMap = mapboxMap
                showAllLand()

                mMapboxMap?.addOnMapClickListener {
                    val pointF = mMapboxMap?.projection?.toScreenLocation(it)
                    if (pointF != null) {
                        val featureList =
                            mMapboxMap?.queryRenderedFeatures(pointF, ALREADY_LAND_FILL_LAYER)
                        if (featureList?.isNotEmpty()!!) {
                            val feature: Feature = featureList[0]
                            val uId = feature.getStringProperty("id")
                            for (entity in mAlreadyLands) {
                                if (entity.id.equals(uId)) {
                                    toast(entity.toString())
                                    break
                                }
                            }
                        }
                    }
                    true
                }
                mMapboxMap?.addOnMapLongClickListener {
                    val pointF = mMapboxMap?.projection?.toScreenLocation(it)
                    if (pointF != null) {
                        val featureList =
                            mMapboxMap?.queryRenderedFeatures(pointF, ALREADY_LAND_FILL_LAYER)
                        if (featureList?.isNotEmpty()!!) {
                            val feature: Feature = featureList[0]
                            val uId = feature.getStringProperty("id")
                            MessageDialog.Builder(this@DrawLandActivity)
                                .setMessage("请对地块进行所对应的操作").setTitle("选择操作")
                                .setConfirm("编辑地块").setCancel("删除地块")
                                .setListener(object : MessageDialog.OnListener {
                                    override fun onConfirm(dialog: BaseDialog?) {
                                        mStartDrawLandView?.text = "退出圈地"
                                        mSaveView?.visibility = View.VISIBLE
                                        mUndoView?.visibility = View.VISIBLE
                                        for (entity in mAlreadyLands) {
                                            if (entity.id.equals(uId)) {
                                                mAlreadyLands.remove(entity)
                                                mBaseDrawMapView?.startDraw(entity,DrawType.DRAW_LAND)
                                                break
                                            }
                                        }
                                        showAllLand()
                                    }

                                    override fun onCancel(dialog: BaseDialog?) {
                                        for (entity in mAlreadyLands) {
                                            if (entity.id.equals(uId)) {
                                                mDeleteLands.add(entity)
                                                mAlreadyLands.remove(entity)
                                                break
                                            }
                                        }
                                        showAllLand()
                                    }
                                }).show()
                        }
                    }
                    true
                }
            }

        })
    }

    private fun showAllLand() {

        mMapboxMap?.style?.apply {
            removeLayer(this)
            removeCenter(this)
            removeLatLng(this)
            removeFourPoint(this)
        }
        if (mAlreadyLands.size <= 0) {
            if (isFirstLoad) {
                mBaseMapView?.startLocation()
            }
            isFirstLoad = false
            return
        }

        val features: MutableList<Feature> = ArrayList()
        val latLngList: MutableList<LatLng?> = ArrayList()
        for (landEntity in mAlreadyLands) {
            val positionss: MutableList<List<Point>> = ArrayList()
            val positions: MutableList<Point> = ArrayList()
            for (latLng in landEntity.latLngs!!) {
                positions.add(Point.fromLngLat(latLng!!.longitude, latLng.latitude))
            }
            latLngList.addAll(landEntity.latLngs!!)
            positionss.add(positions)
            val polygon: Polygon = Polygon.fromLngLats(positionss)
            val `object` = JsonObject()
            `object`.addProperty("id", landEntity.id)
            features.add(Feature.fromGeometry(polygon, `object`))
        }
        val landSource =
            GeoJsonSource(ALREADY_LAND_SOURCE, FeatureCollection.fromFeatures(features))

        val fillLayer = FillLayer(ALREADY_LAND_FILL_LAYER, ALREADY_LAND_SOURCE)
        fillLayer.setProperties(
            PropertyFactory.fillColor(Color.RED), PropertyFactory.fillOpacity(0.2f)
        )
        val lineLayer = LineLayer(ALREADY_LAND_LINE_LAYER, ALREADY_LAND_SOURCE)
        lineLayer.setProperties(
            PropertyFactory.lineColor(Color.RED), PropertyFactory.lineWidth(2f)
        )
        if (isFirstLoad) {
            val bounds = LatLngBounds.Builder().includes(latLngList).build()
            mMapboxMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10))

        }
        mMapboxMap?.style?.apply {
            addSource(landSource)
            addLayer(fillLayer)
            addLayer(lineLayer)
        }

        isFirstLoad = false
        addCenter(mMapboxMap?.style)
        addLatLng(mMapboxMap?.style)
        addFourPoint(mMapboxMap?.style)
    }


    override fun onStart() {
        super.onStart()
        mBaseMapView?.onStart()
    }

    override fun onPause() {
        super.onPause()
        mBaseMapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mBaseMapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mBaseMapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mBaseMapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBaseMapView?.onDestroy()
    }

    private fun addCenter(style: Style?) {
        removeCenter(style)
        val featureList: MutableList<Feature> = java.util.ArrayList()
        for (landEntity in mAlreadyLands) {
            val center = landEntity.center
            val point: Point = Point.fromLngLat(center!!.longitude, center.latitude)
            val jsonObject = JsonObject()
            jsonObject.addProperty(
                "area", """
     ${landEntity.userName}
     ${NumberFormatUtil.doubleRemoveEndZero(String.valueOf(landEntity.area))}亩
     """.trimIndent()
            )
            jsonObject.addProperty("id", landEntity.id)
            featureList.add(Feature.fromGeometry(point, jsonObject))
        }
        val centerJson =
            GeoJsonSource(ALREADY_LAND_CENTER_SOURCE, FeatureCollection.fromFeatures(featureList))
        val textLayer = SymbolLayer(ALREADY_LAND_CENTER_TEXT_LAYER, ALREADY_LAND_CENTER_SOURCE)
        textLayer.setProperties(
            PropertyFactory.textColor(Color.WHITE),
            PropertyFactory.textSize(16F),
            PropertyFactory.textField("{area}")
        )

        style?.apply {
            addSource(centerJson)
            addLayer(textLayer)
        }
    }

    private fun addLatLng(style: Style?) {
        removeLatLng(style)
        if (isShowSelfLand && isShowLatLng && mAlreadyLands.size > 0) {
            val features: MutableList<Feature> = java.util.ArrayList()
            for (landEntity in mAlreadyLands) {
                for (latLng in landEntity.latLngs!!) {
                    val `object` = JsonObject()
                    `object`.addProperty(
                        "point",
                        NumberFormatUtil.pointEight(latLng!!.longitude) + "\n" + NumberFormatUtil.pointEight(
                            latLng.latitude
                        )
                    )
                    features.add(
                        Feature.fromGeometry(
                            Point.fromLngLat(
                                latLng.longitude, latLng.latitude
                            ), `object`
                        )
                    )
                }
            }
            val jsonSource =
                GeoJsonSource(LAND_LATLNG_SOURCE, FeatureCollection.fromFeatures(features))
            val textLayer = SymbolLayer(LAND_LATLNG_LAYER, LAND_LATLNG_SOURCE)
            textLayer.setProperties(
                PropertyFactory.textColor(Color.WHITE),
                PropertyFactory.textSize(10f),
                PropertyFactory.textField("{point}")
            )
            style?.apply {
                addSource(jsonSource)
                addLayer(textLayer)
            }
        }
    }

    // 添加四至点
    private fun addFourPoint(style: Style?) {
        removeFourPoint(style)
        val pointFeature: MutableList<Feature> = java.util.ArrayList()
        val lineFeature: MutableList<Feature> = java.util.ArrayList()
        if (!isShowSelfLand || !isShowFourMap) {
            return
        }
        for (landEntity in mAlreadyLands) {
            val positionList: MutableList<Point> = java.util.ArrayList()
            val latLngBounds = LatLngBounds.Builder().includes(landEntity.latLngs!!).build()
            val northEast = latLngBounds.northEast // 北东
            val northEastObject = JsonObject()
            northEastObject.addProperty(
                "point",
                NumberFormatUtil.pointEight(northEast.longitude) + "\n" + NumberFormatUtil.pointEight(
                    northEast.latitude
                )
            )
            pointFeature.add(
                Feature.fromGeometry(
                    Point.fromLngLat(
                        northEast.longitude, northEast.latitude

                    ), northEastObject
                )
            )
            positionList.add(Point.fromLngLat(northEast.longitude, northEast.latitude))
            val northWest = latLngBounds.northWest // 北西
            val northWestObject = JsonObject()
            northWestObject.addProperty(
                "point",
                NumberFormatUtil.pointEight(northWest.longitude) + "\n" + NumberFormatUtil.pointEight(
                    northWest.latitude
                )
            )
            pointFeature.add(
                Feature.fromGeometry(
                    Point.fromLngLat(
                        northWest.longitude, northWest.latitude

                    ), northWestObject
                )
            )
            positionList.add(Point.fromLngLat(northWest.longitude, northWest.latitude))
            val southEast = latLngBounds.southEast // 南东
            val southEastObject = JsonObject()
            southEastObject.addProperty(
                "point",
                NumberFormatUtil.pointEight(southEast.longitude) + "\n" + NumberFormatUtil.pointEight(
                    southEast.latitude
                )
            )
            pointFeature.add(
                Feature.fromGeometry(
                    Point.fromLngLat(
                        southEast.longitude, southEast.latitude
                    ), southEastObject
                )
            )
            val southWest = latLngBounds.southWest // 南西
            val southWestObject = JsonObject()
            southWestObject.addProperty(
                "point",
                NumberFormatUtil.pointEight(southWest.longitude) + "\n" + NumberFormatUtil.pointEight(
                    southWest.latitude
                )
            )
            pointFeature.add(
                Feature.fromGeometry(
                    Point.fromLngLat(
                        southWest.longitude, southWest.latitude
                    ), southWestObject
                )
            )
            positionList.add(Point.fromLngLat(southWest.longitude, southWest.latitude))
            positionList.add(Point.fromLngLat(southEast.longitude, southEast.latitude))
            positionList.add(Point.fromLngLat(northEast.longitude, northEast.latitude))

            lineFeature.add(Feature.fromGeometry(LineString.fromLngLats(positionList)))
        }
        val lineSource =
            GeoJsonSource(LAND_FOUR_LINE_SOURCE, FeatureCollection.fromFeatures(lineFeature))
        val lineLayer = LineLayer(LAND_FOUR_LINE_LAYER, LAND_FOUR_LINE_SOURCE)
        lineLayer.setProperties(
            PropertyFactory.lineColor(Color.WHITE), PropertyFactory.lineWidth(2f)
        )
        val pointSource =
            GeoJsonSource(LAND_FOUR_POINT_SOURCE, FeatureCollection.fromFeatures(pointFeature))
        val symbolLayer = SymbolLayer(LAND_FOUR_POINT_LAYER, LAND_FOUR_POINT_SOURCE)
        symbolLayer.setProperties(
            PropertyFactory.textField("{point}"),
            PropertyFactory.textColor(Color.WHITE),
            PropertyFactory.textSize(12f)
        )

        style?.apply {
            addSource(lineSource)
            addLayer(lineLayer)
            addSource(pointSource)
            addLayer(symbolLayer)
        }
    }

    private fun removeFourPoint(style: Style?) {
        style?.apply {
            removeLayer(LAND_FOUR_LINE_LAYER)
            removeSource(LAND_FOUR_LINE_SOURCE)
            removeLayer(LAND_FOUR_POINT_LAYER)
            removeSource(LAND_FOUR_POINT_SOURCE)
        }
    }


    private fun removeCenter(style: Style?) {
        style?.apply {
            removeLayer(ALREADY_LAND_CENTER_TEXT_LAYER)
            removeSource(ALREADY_LAND_CENTER_SOURCE)
        }


    }

    private fun removeLatLng(style: Style?) {
        style?.apply {
            removeLayer(LAND_LATLNG_LAYER)
            removeSource(LAND_LATLNG_SOURCE)
        }

    }

    private fun removeLayer(style: Style) {
        style.apply {
            removeLayer(ALREADY_LAND_FILL_LAYER)
            removeLayer(ALREADY_LAND_LINE_LAYER)
            removeSource(ALREADY_LAND_SOURCE)
        }
    }

    companion object {
        private const val ALREADY_LAND_SOURCE = "already_land_source"
        private const val ALREADY_LAND_LINE_LAYER = "already_land_line_layer"
        private const val ALREADY_LAND_FILL_LAYER = "already_land_fill_layer"

        private const val ALREADY_LAND_CENTER_SOURCE = "already_land_center_source"
        private const val ALREADY_LAND_CENTER_TEXT_LAYER = "already_land_center_text_layer"

        private const val LAND_LATLNG_SOURCE = "land_latlng_source"
        private const val LAND_LATLNG_LAYER = "land_latlng_layer"

        // 四至图部分
        private const val LAND_FOUR_POINT_SOURCE = "land_four_point_source"
        private const val LAND_FOUR_POINT_LAYER = "land_four_point_layer"

        private const val LAND_FOUR_LINE_SOURCE = "land_four_line_source"
        private const val LAND_FOUR_LINE_LAYER = "land_four_line_layer"

    }
}