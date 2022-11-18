package com.demo.mapbox.mapview

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.BackgroundLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.RasterLayer
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.style.sources.RasterSource
import com.mapbox.mapboxsdk.style.sources.TileSet
import com.demo.location.location.ILocation
import com.demo.location.location.ILocationListener
import com.demo.location.location.LocationOperations
import com.demo.location.location.transform.TransformGcj02ToWgs84
import com.demo.mapbox.Contract
import com.demo.mapbox.R
import com.demo.mapbox.util.MapViewUtil.hideLogo


/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc: BaseMapView
 */
class BaseMapView @JvmOverloads constructor(
    private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1
) : MapView(
    mContext, attrs, defStyleAttr
) {
    private var mLoadMapListener: LoadMapListener? = null
    private var mLocationOperations: LocationOperations? = null
    private var mLocationListener: ILocationListener? = null
    var map: MapboxMap? = null
        private set
    private var mStyle: Style? = null
    /**
     * 是否显示的矢量底图
     *
     * @return true 显示的是矢量 false 显示的是卫星影像
     */
    /**
     * 是否显示矢量地图
     */
    var isShowVector = true

    // 当前位置
    private var mCurLatLng: LatLng? = null

    /**
     * 初始化 mapView
     */
    fun initMapView() {
        // 加载失败 处理
        addOnDidFailLoadingMapListener { errorMessage: String? ->
//            error(TAG, errorMessage!!)
            if (mLoadMapListener != null) {
                mLoadMapListener!!.onFailed(400, errorMessage)
            }
        }
        // 开始加载
        addOnWillStartLoadingMapListener {
            if (mLoadMapListener != null) {
                mLoadMapListener!!.onLoading()
            }
        }
        // 加载成功
        addOnDidFinishLoadingMapListener {
            if (mLoadMapListener != null) {
                mLoadMapListener!!.onLoadComplete()
            }
        }
        getMapAsync { mapboxMap: MapboxMap ->
            map = mapboxMap
            mapboxMap.setStyle(Style.SATELLITE_STREETS) { style: Style? ->
                mStyle = style
                hideLogo(map!!)
                initTdtLayer()
                initLocation()
                addImage()
            }
        }
    }

    private fun addImage() {
        mStyle?.addImage(
            "location_image",
            BitmapFactory.decodeResource(mContext.resources, R.mipmap.current_location)
        )
    }

    /**
     * 初始化定位操作
     */
    private fun initLocation() {
        mLocationOperations = LocationOperations(context.applicationContext)
        // 设置时间间隔
        mLocationOperations!!.setInterval(3000)
        mLocationOperations!!.setTransform(TransformGcj02ToWgs84())
        mLocationOperations!!.setLocationListener(object : ILocationListener {
            override fun onLocationSuccess(location: ILocation) {
                mLocationOperations!!.stop()
                mCurLatLng = LatLng(location.latitude, location.longitude)
                addLocationMarker()
                moveToLocation()
                if (mLocationListener != null) {
                    mLocationListener!!.onLocationSuccess(location)
                }
            }

            override fun onLocationFailure(message: String?) {
                Log.e(TAG, message!!)
                if (mLocationListener != null) {
                    mLocationListener!!.onLocationFailure(message)
                }
            }
        })
    }

    /**
     * 添加当前位置的marker
     */
    private fun addLocationMarker() {
        if (mCurLatLng == null) {
            return
        }
        val point = Point.fromLngLat(mCurLatLng!!.longitude, mCurLatLng!!.latitude)
        val feature = Feature.fromGeometry(point)
        if (mStyle!!.getSource(LOCATION_SOURCE) == null) {
            val jsonSource = GeoJsonSource(LOCATION_SOURCE, feature)
            mStyle!!.addSource(jsonSource)
            val layer = SymbolLayer(LOCATION_LAYER, LOCATION_SOURCE)
            layer.setProperties(
                PropertyFactory.iconImage("location_image")
            )
            mStyle!!.addLayerAbove(layer, Contract.BASE_EMPTY_LAYER)
        } else {
            (mStyle!!.getSource(LOCATION_SOURCE) as GeoJsonSource?)!!.setGeoJson(feature)
        }
    }

    /**
     * 移动过去
     */
    private fun moveToLocation() {
        if (mCurLatLng == null) {
            return
        }
        val position = CameraPosition.Builder().target(mCurLatLng).zoom(16.0).build()
        map!!.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000)
    }

    /**
     * 添加天地图 layer
     */
    private fun initTdtLayer() {
//        addDigitalLayer()
        addSatelliteLayer()
        addRoadLayer()
        addZhuJiLayer()

        addXingTuImgLayer()
        addXingTuCiaLayer()

        switchImage("Mapbox")
    }

    /**
     * 添加注记
     */
    private fun addZhuJiLayer() {
        val tileSet = TileSet("8", Contract.ZHUJI_SOURCE_URL)
        tileSet.maxZoom = 17f
        val roadSource = RasterSource(ZHU_JI_SOURCE, tileSet, 256)
        mStyle!!.addSource(roadSource)
        val roadLayer = RasterLayer(ZHU_JI_LAYER, ZHU_JI_SOURCE)
        mStyle!!.addLayer(roadLayer)
    }

    /**
     * 添加路网 layer
     */
    private fun addRoadLayer() {
        val tileSet = TileSet("8", Contract.ROAD_SOURCE_URL)
        tileSet.maxZoom = 17f
        val roadSource = RasterSource(ROAD_SOURCE, tileSet, 256)
        mStyle!!.addSource(roadSource)
        val roadLayer = RasterLayer(ROAD_LAYER, ROAD_SOURCE)
        mStyle!!.addLayer(roadLayer)
    }

    /**
     * 添加卫星影像layer
     */
    private fun addSatelliteLayer() {
        val tileSet = TileSet("8", Contract.SATELLITE_SOURCE_URL)
        tileSet.maxZoom = 17f
        val satelliteSource = RasterSource(SATELLITE_SOURCE, tileSet, 256)
        mStyle!!.addSource(satelliteSource)
        val satelliteLayer = RasterLayer(SATELLITE_LAYER, SATELLITE_SOURCE)
        mStyle!!.addLayer(satelliteLayer)
    }

    /**
     * 添加电子地图 layer
     */
    private fun addDigitalLayer() {
        val tileSet = TileSet("8", Contract.DIGITAL_SOURCE_URL)
        tileSet.maxZoom = 17f
        val digitalSource = RasterSource(DIGITAL_SOURCE, tileSet, 256)
        mStyle!!.addSource(digitalSource)
        val digitalLayer = RasterLayer(DIGITAL_LAYER, DIGITAL_SOURCE)
        mStyle!!.addLayerBelow(digitalLayer, Contract.BASE_EMPTY_LAYER)
    }

    private fun addXingTuImgLayer() {
        val tileSet = TileSet("8", Contract.XINGTU_IMG_SOURCE_URL)
        tileSet.maxZoom = 17f
        val digitalSource = RasterSource(XINGTU_IMG_SOURCE, tileSet, 256)
        mStyle!!.addSource(digitalSource)
        val digitalLayer = RasterLayer(XINGTU_IMG_LAYER, XINGTU_IMG_SOURCE)
        mStyle!!.addLayer(digitalLayer)
    }

    private fun addXingTuCiaLayer() {
        val tileSet = TileSet("8", Contract.XINGTU_CIA_SOURCE_URL)
        tileSet.maxZoom = 17f
        val digitalSource = RasterSource(XINGTU_CIA_SOURCE, tileSet, 256)
        mStyle!!.addSource(digitalSource)
        val digitalLayer = RasterLayer(XINGTU_CIA_LAYER, XINGTU_CIA_SOURCE)
        mStyle!!.addLayer(digitalLayer)
    }

    /**
     * 添加空的 layer
     */
    private fun addEmptyLayer() {
        val layer = BackgroundLayer(Contract.BASE_EMPTY_LAYER)
        layer.setProperties(
            PropertyFactory.backgroundColor(Color.TRANSPARENT)
        )
        mStyle!!.addLayer(layer)
    }

    /**
     * 切换底图
     */
    fun changeImageLayer(name: String) {
        switchImage(name)
    }

    /**
     * 切换底图
     */
    private fun switchImage(name: String) {

        when (name) {
            "Mapbox" -> {
                mStyle?.getLayer(XINGTU_IMG_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.NONE))
                mStyle?.getLayer(XINGTU_CIA_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.NONE))
                mStyle?.getLayer(SATELLITE_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.NONE))
                mStyle?.getLayer(ROAD_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.NONE))
                mStyle?.getLayer(ZHU_JI_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.NONE))
            }

            "天地图" -> {
                mStyle?.getLayer(XINGTU_IMG_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.NONE))
                mStyle?.getLayer(XINGTU_CIA_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.NONE))
                mStyle?.getLayer(SATELLITE_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.VISIBLE))
                mStyle?.getLayer(ROAD_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.VISIBLE))
                mStyle?.getLayer(ZHU_JI_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.VISIBLE))
            }

            "星图地球" -> {
                mStyle?.getLayer(XINGTU_IMG_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.VISIBLE))
                mStyle?.getLayer(XINGTU_CIA_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.VISIBLE))
                mStyle?.getLayer(SATELLITE_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.NONE))
                mStyle?.getLayer(ROAD_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.NONE))
                mStyle?.getLayer(ZHU_JI_LAYER)
                    ?.setProperties(PropertyFactory.visibility(Property.NONE))
            }
        }


    }

    /**
     * 开始定位
     */
    fun startLocation() {
        mLocationOperations?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationOperations != null) {
            mLocationOperations!!.destroy()
        }
    }

    /**
     * 设置地图加载监听
     *
     * @param listener listener
     */
    fun setLoadMapListener(listener: LoadMapListener?) {
        mLoadMapListener = listener
    }

    /**
     * 设置位置监听
     *
     * @param listener listener
     */
    fun setLocationListener(listener: ILocationListener?) {
        mLocationListener = listener
    }

    /**
     * 获取地图的中心点
     *
     * @return 地图的中心点
     */
    val center: LatLng
        get() = map!!.cameraPosition.target

    /**
     * 设置地图的中心点
     *
     * @param center         中心点信息
     * @param cameraPosition 一些信息
     */
    fun setCenter(center: LatLng?, cameraPosition: CameraPosition) {
        val tarPosition = CameraPosition.Builder().target(center).zoom(cameraPosition.zoom)
            .bearing(cameraPosition.bearing).tilt(cameraPosition.tilt).build()
        map!!.moveCamera(CameraUpdateFactory.newCameraPosition(tarPosition))
    }

    companion object {
        private const val TAG = "BaseMapView"

        // 定位点的source和layer
        private const val LOCATION_SOURCE = "location_source"
        private const val LOCATION_LAYER = "location_layer"

        // 电子地图
        private const val DIGITAL_SOURCE = "digital_source"
        private const val DIGITAL_LAYER = "digital_layer"

        // 卫星地图
        private const val SATELLITE_SOURCE = "satellite_source"
        private const val SATELLITE_LAYER = "satellite_layer"

        // 路网
        private const val ROAD_SOURCE = "road_source"
        private const val ROAD_LAYER = "road_layer"

        // 注记
        private const val ZHU_JI_SOURCE = "zhu_ji_source"
        private const val ZHU_JI_LAYER = "zhu_ji_layer"

        private const val XINGTU_IMG_SOURCE = "xingtu_img_source"
        private const val XINGTU_IMG_LAYER = "xingtu_img_layer"

        private const val XINGTU_CIA_SOURCE = "xingtu_cia_source"
        private const val XINGTU_CIA_LAYER = "xingtu_cia_layer"
    }
}