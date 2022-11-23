package com.demo.drawland.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import com.amap.api.services.poisearch.PoiResultV2
import com.demo.base.BaseDialog
import com.demo.drawland.LandEntity
import com.demo.drawland.R
import com.demo.drawland.aop.SingleClick
import com.demo.drawland.app.TitleBarFragment
import com.demo.drawland.ui.activity.HomeActivity
import com.demo.drawland.ui.dialog.SelectDialog
import com.demo.location.poi.IPoiSearchListener
import com.demo.location.poi.PoiSearchOperations
import com.demo.mapbox.draw.BaseDrawMapView
import com.demo.mapbox.draw.manager.DrawType
import com.demo.mapbox.mapview.BaseMapView
import com.demo.mapbox.util.AssetsUtil
import com.demo.mapbox.util.NumberFormatUtil
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.hjq.gson.factory.GsonFactory
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

/**
 * author :hujw
 * time : 2022/11/14
 * desc : 首页 Fragment
 */
class MapboxFragment : TitleBarFragment<HomeActivity>() {

    companion object {
        var layerPos = 0
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

        fun newInstance(): MapboxFragment {
            return MapboxFragment()
        }
    }

    private var mBaseDrawMapView: BaseDrawMapView? =null
    private var mBaseMapView: BaseMapView? = null

    private var mMap: MapboxMap? = null


    // 已有的地块
    private val mAlreadyLands = mutableListOf<LandEntity>()

    // 删除的地块
    private val mDeleteLands = mutableListOf<LandEntity>()

    // 第一次加载
    private var isFirstLoad = true

    private val isShowSelfLand = true
    private val isShowLatLng = false
    private val isShowFourMap = true

    override fun getLayoutId(): Int {
        return R.layout.mapbox_fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBaseDrawMapView?.onCreate(savedInstanceState)
    }

    override fun initView() {
        mBaseDrawMapView=findViewById(R.id.base_draw_map_view)
        mBaseDrawMapView?.initDrawMapView()
        mBaseMapView = mBaseDrawMapView?.baseMapView

        setOnClickListener(R.id.iv_layer_manager, R.id.iv_location)

        findViewById<View>(R.id.btn_location1)?.setOnClickListener { v: View? ->
            val operations = PoiSearchOperations(context)
            operations.setPoiSearchListener(object : IPoiSearchListener {
                override fun startSearch() {}
                override fun searchFailure() {}
                override fun searchSuccess(poiResultV2: PoiResultV2) {
                    val pois = poiResultV2.pois
                    for (poiItemV2 in pois) {
                        Log.i(
                            "gyl",
                            poiItemV2.title + "," + poiItemV2.latLonPoint.longitude + "," + poiItemV2.latLonPoint.latitude
                        )
                    }
                }
            })
            operations.startSearch("山西省万荣县薛吉村", 10, 1)
        }
        findViewById<View>(R.id.btn_draw_land)?.setOnClickListener {
            mBaseDrawMapView?.startDraw(
                DrawType.DRAW_LAND
            )
        }
        findViewById<View>(R.id.btn_add_point)?.setOnClickListener {
            mBaseDrawMapView?.addPoint(
                mBaseDrawMapView?.baseMapView!!.center
            )
        }
        findViewById<View>(R.id.btn_delete)?.setOnClickListener { mBaseDrawMapView?.delete() }
        findViewById<View>(R.id.btn_undo)?.setOnClickListener { mBaseDrawMapView?.undo() }
        findViewById<View>(R.id.btn_complete)?.setOnClickListener {
            mBaseDrawMapView?.complete()
        }
        findViewById<View>(R.id.btn_draw_length)?.setOnClickListener {
            mBaseDrawMapView?.startDraw(
                DrawType.MEASURE_LENGTH
            )
        }
        findViewById<View>(R.id.btn_draw_area)?.setOnClickListener {
            mBaseDrawMapView?.startDraw(
                DrawType.MEASURE_AREA
            )
        }
    }

    override fun initData() {
        val landData = AssetsUtil.loadStringFromAssets(requireContext(), "land.json")

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
    }


    @SingleClick
    override fun onClick(view: View) {
        when (view.id) {
            R.id.iv_layer_manager -> {
                SelectDialog.Builder(requireContext()).setTitle("请选择底图")
                    .setList("Mapbox", "天地图", "星图地球")
                    // 设置单选模式
                    .setSingleSelect()
                    // 设置默认选中
                    .setSelect(layerPos).setListener(object : SelectDialog.OnListener<String> {
                        override fun onSelected(dialog: BaseDialog?, data: HashMap<Int, String>) {
                            data.keys.forEach {
                                when (it) {
                                    0 -> {
                                        mBaseDrawMapView?.changeImageLayer("Mapbox")
                                    }

                                    1 -> {
                                        mBaseDrawMapView?.changeImageLayer("天地图")
                                    }

                                    2 -> {
                                        mBaseDrawMapView?.changeImageLayer("星图地球")
                                    }
                                }
                                layerPos = it
                            }
                        }

                        override fun onCancel(dialog: BaseDialog?) {

                        }
                    }).show()
            }

            R.id.iv_location -> {
                mBaseDrawMapView?.baseMapView?.startLocation()
            }
        }
    }

    private fun showAllLand() {
        mMap?.style?.apply {
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
        val lineLayer = LineLayer(
            ALREADY_LAND_LINE_LAYER,
            ALREADY_LAND_SOURCE
        )
        lineLayer.setProperties(
            PropertyFactory.lineColor(Color.RED), PropertyFactory.lineWidth(2f)
        )
        if (isFirstLoad) {
            val bounds = LatLngBounds.Builder().includes(latLngList).build()
            mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10))

        }
        mMap?.style?.apply {
            addSource(landSource)
            addLayer(fillLayer)
            addLayer(lineLayer)
        }

        isFirstLoad = false
        addCenter(mMap?.style)
        addLatLng(mMap?.style)
        addFourPoint(mMap?.style)
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
     ${NumberFormatUtil.doubleRemoveEndZero(java.lang.String.valueOf(landEntity.area))}亩
     """.trimIndent()
            )
            jsonObject.addProperty("id", landEntity.id)
            featureList.add(Feature.fromGeometry(point, jsonObject))
        }
        val centerJson = GeoJsonSource(
            ALREADY_LAND_CENTER_SOURCE, FeatureCollection.fromFeatures(featureList)
        )
        val textLayer = SymbolLayer(
            ALREADY_LAND_CENTER_TEXT_LAYER,
            ALREADY_LAND_CENTER_SOURCE
        )
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
            val jsonSource = GeoJsonSource(
                LAND_LATLNG_SOURCE, FeatureCollection.fromFeatures(features)
            )
            val textLayer =
                SymbolLayer(LAND_LATLNG_LAYER, LAND_LATLNG_SOURCE)
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
        val lineSource = GeoJsonSource(
            LAND_FOUR_LINE_SOURCE, FeatureCollection.fromFeatures(lineFeature)
        )
        val lineLayer =
            LineLayer(LAND_FOUR_LINE_LAYER, LAND_FOUR_LINE_SOURCE)
        lineLayer.setProperties(
            PropertyFactory.lineColor(Color.WHITE), PropertyFactory.lineWidth(2f)
        )
        val pointSource = GeoJsonSource(
            LAND_FOUR_POINT_SOURCE, FeatureCollection.fromFeatures(pointFeature)
        )
        val symbolLayer = SymbolLayer(
            LAND_FOUR_POINT_LAYER, LAND_FOUR_POINT_SOURCE
        )
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


    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }


    override fun onPause() {
        mBaseDrawMapView?.onPause()
        super.onPause()
    }

    override fun onResume() {
        mBaseDrawMapView?.onResume()
        super.onResume()
    }


    override fun onDestroy() {
        mBaseDrawMapView?.onDestroy()
        super.onDestroy()

    }


}