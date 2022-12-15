package com.demo.drawland

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.xdzt.base.BaseDialog
import com.demo.drawland.aop.SingleClick
import com.demo.drawland.app.AppActivity
import com.demo.drawland.ui.dialog.MessageDialog
import com.demo.drawland.ui.dialog.SelectDialog
import com.xdzt.mapbox.LandEntity
import com.xdzt.mapbox.draw.BaseDrawMapView
import com.xdzt.mapbox.draw.LoadDrawMapViewListener
import com.xdzt.mapbox.draw.manager.DrawType
import com.xdzt.mapbox.mapview.BaseMapView
import com.xdzt.mapbox.util.AssetsUtil
import com.xdzt.mapbox.util.GeometryUtil
import com.xdzt.mapbox.util.NumberFormatUtil
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.hjq.gson.factory.GsonFactory
import com.hjq.shape.view.ShapeImageView
import com.hjq.shape.view.ShapeTextView
import com.hjq.toast.ToastUtils
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.fillLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.OnMapLongClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
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
class DrawLandActivity : AppActivity(), OnMapClickListener, OnMapLongClickListener {

    private var mMapboxMap: MapboxMap? = null

    private val mBaseDrawMapView: BaseDrawMapView? by lazy { findViewById(R.id.base_draw_map_view) }
    private var mBaseMapView: BaseMapView? = null
    private val mLocationView: ShapeImageView? by lazy { findViewById(R.id.iv_location) }
    private val mLayerManagerView: ShapeImageView? by lazy { findViewById(R.id.iv_layer_manager) }

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

    //图层
    private var isLayerManager = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBaseDrawMapView?.onCreate(savedInstanceState)
    }

    /**
     * 初始化布局
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_draw_land_for_mapbox
    }

    /**
     * 初始化View
     */
    override fun initView() {
        mBaseDrawMapView?.initDrawMapView()
        mBaseMapView = mBaseDrawMapView?.baseMapView?.hideLogo()?.hideScaleBar()
        //指南针
        mBaseMapView?.initCompassUI(true, null, Gravity.LEFT)
        setOnClickListener(
            mLocationView, mStartDrawLandView, mSaveView, mUndoView, mLayerManagerView
        )


    }


    /**
     * 初始化数据
     */
    override fun initData() {


        val landData = AssetsUtil.loadStringFromAssets(this@DrawLandActivity, "land.json")

        val landEntityList: MutableList<LandEntity> = GsonFactory.getSingletonGson()
            .fromJson(landData, object : TypeToken<MutableList<LandEntity>>() {}.type)
        mAlreadyLands.addAll(landEntityList)
        mAlreadyLands.forEach {
            it.state = 0
            val paths: List<LandEntity.PathBean>? = it.path
            val points: MutableList<Point?> = ArrayList()
            if (paths != null) {
                for (path in paths) {
                    val latLng = Point.fromLngLat(path.longitude, path.latitude)
                    points.add(latLng)
                }
            }
            it.center = GeometryUtil.getCenter(points)
            it.latLngs = points
        }

        /**
         * 加载完地图的回调
         */
        mBaseDrawMapView?.setOnLoadDrawMapViewListener(object : LoadDrawMapViewListener {
            override fun loadSuccess(mapboxMap: MapboxMap?) {
                mMapboxMap = mapboxMap
                //显示所有地块
                showAllLand()
                mMapboxMap?.apply {
                    //地图点击事件
                    addOnMapClickListener(this@DrawLandActivity)
                    //地图长按事件
                    addOnMapLongClickListener(this@DrawLandActivity)
                }
            }
        })
    }

    /**
     * 显示所有地块
     */
    private fun showAllLand() {
        mMapboxMap?.getStyle()?.apply {
            removeLayer()
            removeCenter()
            removeLatLng()
            removeFourPoint()
        }
        if (mAlreadyLands.size <= 0) {
            if (isFirstLoad) {
                mBaseMapView?.startLocation()
            }
            isFirstLoad = false
            return
        }

        val features: MutableList<Feature> = ArrayList()
        val points: MutableList<Point?> = ArrayList()
        for (landEntity in mAlreadyLands) {
            val positionss: MutableList<List<Point>> = ArrayList()
            val positions: MutableList<Point> = ArrayList()
            for (latLng in landEntity.latLngs!!) {
                positions.add(Point.fromLngLat(latLng!!.longitude(), latLng.latitude()))
            }
            points.addAll(landEntity.latLngs!!)
            positionss.add(positions)
            val polygon: Polygon = Polygon.fromLngLats(positionss)
            val `object` = JsonObject()
            `object`.addProperty("id", landEntity.id)
            features.add(Feature.fromGeometry(polygon, `object`))
        }
        mMapboxMap?.getStyle()?.apply {
            addSource(geoJsonSource(ALREADY_LAND_SOURCE) {
                featureCollection(FeatureCollection.fromFeatures(features))
            })
            addLayer(fillLayer(ALREADY_LAND_FILL_LAYER, ALREADY_LAND_SOURCE) {
                fillColor(Color.RED)
                fillOpacity(0.2)
            })
            addLayer(lineLayer(ALREADY_LAND_LINE_LAYER, ALREADY_LAND_SOURCE) {
                lineColor(Color.RED)
                lineWidth(2.0)
            })
        }

        if (isFirstLoad) {
            mBaseMapView?.moveCameraTo(points.first(), 16.0, 1500)
        }

        isFirstLoad = false
        addCenter()
        addLatLng()
        addFourPoint()
    }


    @SingleClick
    override fun onClick(view: View) {
        when (view) {
            mLocationView -> {
                mBaseMapView?.startLocation()
            }

            mLayerManagerView -> {
                mLayerManagerView?.setImageDrawable(resources.getDrawable(R.drawable.ic_map_on_layer))
                SelectDialog.Builder(this).setTitle("请选择底图")
                    .setList("星图地球", "Mapbox", "天地图")
                    // 设置单选模式
                    .setSingleSelect()
                    // 设置默认选中
                    .setSelect(layerPos).setListener(object : SelectDialog.OnListener<String> {
                        override fun onSelected(dialog: BaseDialog?, data: HashMap<Int, String>) {
                            data.forEach {
                                mBaseDrawMapView?.changeImageLayer(it.value.toString())
                                layerPos = it.key
                            }
                            mLayerManagerView?.setImageDrawable(resources.getDrawable(R.drawable.ic_map_off_layer))
                        }

                        override fun onCancel(dialog: BaseDialog?) {
                            mLayerManagerView?.setImageDrawable(resources.getDrawable(R.drawable.ic_map_off_layer))
                        }
                    }).show()
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
                if (land?.pointList?.size!! < 3) {
                    ToastUtils.show(resources.getString(R.string.edit_is_un_closed_farmland))
                    return
                }
                if (!land.pointList.first()?.equals(land.pointList.last())!!) {
                    val firstPoint = land.pointList.first()
                    land.pointList.add(
                        Point.fromLngLat(
                            firstPoint?.longitude() ?: 0.0, firstPoint?.latitude() ?: 0.0
                        )
                    )
                }

                landEntity.latLngs = land.pointList
                landEntity.area = land.area?.toDouble()!!
                landEntity.userName = "李四"
                landEntity.center =
                    mMapboxMap?.cameraForCoordinates(landEntity.latLngs as List<Point>)?.center
                val newPath = mutableListOf<LandEntity.PathBean>()

                land.pointList.forEach {
                    val bean = LandEntity.PathBean()
                    bean.longitude = it!!.longitude()
                    bean.latitude = it.latitude()
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


    override fun onStart() {
        super.onStart()
        mBaseMapView?.onStart()
    }


    override fun onStop() {
        super.onStop()
        mBaseMapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mBaseMapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBaseMapView?.onDestroy()
    }

    private fun addCenter() {
        removeCenter()
        val featureList: MutableList<Feature> = ArrayList()
        for (landEntity in mAlreadyLands) {
            val center = landEntity.center
            val point: Point = Point.fromLngLat(center!!.longitude(), center.latitude())
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
        mMapboxMap?.getStyle()?.apply {
            addSource(geoJsonSource(ALREADY_LAND_CENTER_SOURCE) {
                featureCollection(FeatureCollection.fromFeatures(featureList))
            })
            addLayer(symbolLayer(ALREADY_LAND_CENTER_TEXT_LAYER, ALREADY_LAND_CENTER_SOURCE) {
                textColor(Color.WHITE)
                textSize(16.0)
                textField("{area}")
            })
        }
    }

    private fun addLatLng() {
        removeLatLng()
        if (isShowSelfLand && isShowLatLng && mAlreadyLands.size > 0) {
            val features: MutableList<Feature> = java.util.ArrayList()
            for (landEntity in mAlreadyLands) {
                for (latLng in landEntity.latLngs!!) {
                    val `object` = JsonObject()
                    `object`.addProperty(
                        "point",
                        NumberFormatUtil.pointEight(latLng!!.longitude()) + "\n" + NumberFormatUtil.pointEight(
                            latLng.latitude()
                        )
                    )
                    features.add(
                        Feature.fromGeometry(
                            Point.fromLngLat(
                                latLng.longitude(), latLng.latitude()
                            ), `object`
                        )
                    )
                }
            }

            mMapboxMap?.getStyle()?.apply {
                addSource(geoJsonSource(LAND_LATLNG_SOURCE) {
                    featureCollection(FeatureCollection.fromFeatures(features))
                })
                addLayer(symbolLayer(LAND_LATLNG_LAYER, LAND_LATLNG_SOURCE) {
                    textColor(Color.WHITE)
                    textSize(10.0)
                    textField("{point}")
                })
            }
        }
    }

    /**
     * 四至
     * 这个只能获取到图形的中心点，获取不到东南 西南 西北 东北的经纬度
     * val cameraOptions = mMapboxMap?.cameraForCoordinates(landEntity.latLngs as List<Point>)
     * cameraOptions.center()
     */
    private fun addFourPoint() {
        removeFourPoint()
        val pointFeature: MutableList<Feature> = java.util.ArrayList()
        val lineFeature: MutableList<Feature> = java.util.ArrayList()
        if (!isShowSelfLand || !isShowFourMap) {
            return
        }
        for (landEntity in mAlreadyLands) {
            val positionList: MutableList<Point> = ArrayList()
            val latLngBounds = mMapboxMap?.getBounds()?.bounds
            val cameraOptions = mMapboxMap?.cameraForCoordinates(landEntity.latLngs as List<Point>)
//            val latLngBounds = LatLngBounds.Builder().includes(landEntity.latLngs!!).build()
            val northEast = latLngBounds?.northwest() // 北东
            val northEastObject = JsonObject()
            northEastObject.addProperty(
                "point",
                NumberFormatUtil.pointEight(northEast!!.longitude()) + "\n" + NumberFormatUtil.pointEight(
                    northEast.latitude()
                )
            )
            pointFeature.add(
                Feature.fromGeometry(
                    Point.fromLngLat(
                        northEast.longitude(), northEast.latitude()

                    ), northEastObject
                )
            )
            positionList.add(Point.fromLngLat(northEast.longitude(), northEast.latitude()))
            val northWest = latLngBounds.northwest() // 北西
            val northWestObject = JsonObject()
            northWestObject.addProperty(
                "point",
                NumberFormatUtil.pointEight(northWest.longitude()) + "\n" + NumberFormatUtil.pointEight(
                    northWest.latitude()
                )
            )
            pointFeature.add(
                Feature.fromGeometry(
                    Point.fromLngLat(
                        northWest.longitude(), northWest.latitude()
                    ), northWestObject
                )
            )
            positionList.add(Point.fromLngLat(northWest.longitude(), northWest.latitude()))
            val southEast = latLngBounds.southwest // 南东
            val southEastObject = JsonObject()
            southEastObject.addProperty(
                "point",
                NumberFormatUtil.pointEight(southEast.longitude()) + "\n" + NumberFormatUtil.pointEight(
                    southEast.latitude()
                )
            )
            pointFeature.add(
                Feature.fromGeometry(
                    Point.fromLngLat(
                        southEast.longitude(), southEast.latitude()
                    ), southEastObject
                )
            )
            val southWest = latLngBounds.southeast() // 南西
            val southWestObject = JsonObject()
            southWestObject.addProperty(
                "point",
                NumberFormatUtil.pointEight(southWest.longitude()) + "\n" + NumberFormatUtil.pointEight(
                    southWest.latitude()
                )
            )
            pointFeature.add(
                Feature.fromGeometry(
                    Point.fromLngLat(
                        southWest.longitude(), southWest.latitude()
                    ), southWestObject
                )
            )
            positionList.add(Point.fromLngLat(southWest.longitude(), southWest.latitude()))
            positionList.add(Point.fromLngLat(southEast.longitude(), southEast.latitude()))
            positionList.add(Point.fromLngLat(northEast.longitude(), northEast.latitude()))

            lineFeature.add(Feature.fromGeometry(LineString.fromLngLats(positionList)))
        }

        mMapboxMap?.getStyle()?.apply {
            addSource(geoJsonSource(LAND_FOUR_LINE_SOURCE) {
                featureCollection(FeatureCollection.fromFeatures(lineFeature))
            })
            addLayer(lineLayer(LAND_FOUR_LINE_LAYER, LAND_FOUR_LINE_SOURCE) {
                lineColor(Color.WHITE)
                lineWidth(2.0)
            })
            addSource(geoJsonSource(LAND_FOUR_POINT_SOURCE) {
                featureCollection(FeatureCollection.fromFeatures(pointFeature))
            })
            addLayer(symbolLayer(LAND_FOUR_POINT_LAYER, LAND_FOUR_POINT_SOURCE) {
                textField("{point}")
                textColor(Color.WHITE)
                textSize(12.0)
            })
        }
    }

    private fun removeFourPoint() {
        mMapboxMap?.getStyle()?.apply {
            removeStyleLayer(LAND_FOUR_LINE_LAYER)
            removeStyleSource(LAND_FOUR_LINE_SOURCE)
            removeStyleLayer(LAND_FOUR_POINT_LAYER)
            removeStyleSource(LAND_FOUR_POINT_SOURCE)
        }
    }


    private fun removeCenter() {
        mMapboxMap?.getStyle()?.apply {
            removeStyleLayer(ALREADY_LAND_CENTER_TEXT_LAYER)
            removeStyleSource(ALREADY_LAND_CENTER_SOURCE)
        }


    }

    private fun removeLatLng() {
        mMapboxMap?.getStyle()?.apply {
            removeStyleLayer(LAND_LATLNG_LAYER)
            removeStyleSource(LAND_LATLNG_SOURCE)
        }

    }

    private fun removeLayer() {
        mMapboxMap?.getStyle()?.apply {
            removeStyleLayer(ALREADY_LAND_FILL_LAYER)
            removeStyleLayer(ALREADY_LAND_LINE_LAYER)
            removeStyleSource(ALREADY_LAND_SOURCE)
        }
    }

    companion object {
        private var layerPos = 0

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

    /**
     * 地图点击事件
     */
    override fun onMapClick(point: Point): Boolean {
        //查询地块信息
        mBaseMapView?.getFeature(ALREADY_LAND_FILL_LAYER, point, onFeatureClicked = {
            val uId = it.getStringProperty("id")
            for (entity in mAlreadyLands) {
                if (entity.id!! == uId) {
                    toast(it.toString())
                    break
                }
            }
        })
        return true
    }

    /**
     * 地图长按事件
     */
    override fun onMapLongClick(point: Point): Boolean {
        mBaseMapView?.getFeature(ALREADY_LAND_FILL_LAYER, point, onFeatureClicked = {
            val uId = it.getStringProperty("id")
            MessageDialog.Builder(this@DrawLandActivity).setMessage("请对地块进行所对应的操作")
                .setTitle("选择操作").setConfirm("编辑地块").setCancel("删除地块")
                .setListener(object : MessageDialog.OnListener {
                    override fun onConfirm(dialog: BaseDialog?) {
                        mStartDrawLandView?.text = "退出圈地"
                        mSaveView?.visibility = View.VISIBLE
                        mUndoView?.visibility = View.VISIBLE
                        for (entity in mAlreadyLands) {
                            if (entity.id.equals(uId)) {
                                mAlreadyLands.remove(entity)
                                mBaseDrawMapView?.startDraw(entity, DrawType.DRAW_LAND)
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
        })
        return true
    }
}