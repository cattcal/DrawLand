package com.demo.arcgis.mapview

import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import android.text.TextUtils
import com.esri.arcgisruntime.mapping.view.Graphic
import kotlin.jvm.JvmOverloads
import android.widget.FrameLayout
import android.view.LayoutInflater
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import com.esri.arcgisruntime.mapping.Viewpoint
import com.demo.arcgis.mapview.tdt.TdtLayerType
import com.esri.arcgisruntime.layers.WebTiledLayer
import com.demo.arcgis.mapview.tdt.CreateTdtTileLayer
import com.demo.location.location.LocationOperations
import com.esri.arcgisruntime.layers.ImageTiledLayer
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.loadable.LoadStatus
import com.demo.location.location.transform.TransformGcj02ToWgs84
import com.demo.location.location.ILocation
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol
import android.util.AttributeSet
import android.util.Log
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.location.AndroidLocationDataSource
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.LoadSettings
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.esri.arcgisruntime.security.UserCredential
import com.demo.arcgis.R
import com.demo.location.location.ILocationListener
import com.demo.arcgis.mapview.layer.cache.MapBoxCacheTiledLayer
import com.demo.arcgis.mapview.layer.cache.TianDiTuTiledLayer
import kotlin.Exception

/**
 * @author guoyalong
 * @time 2022/11/03
 * @desc arcgis baseMapView
 */
class BaseMapView @JvmOverloads constructor(
    private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1
) : FrameLayout(
    mContext, attrs, defStyleAttr
) {
    private var mLoadMapListener: LoadMapListener? = null
    private var mLocationOperations: LocationOperations? = null

    // marker ??????
    private var mGraphicsOverlay: GraphicsOverlay? = null


    /**
     * ??????mapView??????
     *
     * @return mapView
     */
    val mapView: MapView

    //ArcGisMap
    val arcGISMap = ArcGISMap()


    // ??????????????????
    private var mUserCredential: UserCredential? = null

    // ????????????
    private var mImageLabelLayer: WebTiledLayer? = null
    private var mImageLayer: ImageTiledLayer? = null

    // ????????????
    private var mVectorLabelLayer: WebTiledLayer? = null
    private var mVectorLayer: WebTiledLayer? = null
    private val mMaxScale: Double = CreateTdtTileLayer.getScaleByLevel(19)
    /**
     * ???????????????????????????
     *
     * @return true ?????????????????? false ????????????????????????
     */
    /**
     * ????????????????????????
     */
    var isShowVector = true
        private set
    private var mLocationListener: ILocationListener? = null

    /**
     * ????????????
     */
    private var mCurPoint: com.esri.arcgisruntime.geometry.Point? = null

    init {
        // ???arcgis ??????
        ArcGISRuntimeEnvironment.setApiKey(mContext.getString(R.string.arcgis_key))
        // ??????arcgis?????????
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud4449636536,none,NKMFA0PL4S0DRJE15166")
        val view = LayoutInflater.from(mContext).inflate(R.layout.base_map_view_layout, this)
        mapView = view.findViewById(R.id.mapView)
    }

    /**
     * ???????????????????????????????????????????????????
     *
     *
     * ??????????????????????????????arcgis??????
     *
     * @param username ?????????
     * @param password ??????
     */
    @SuppressLint("ClickableViewAccessibility")
    fun initMap(username: String?, password: String?) {
        var username = username
        var password = password
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            // TODO ??????userName ???password
            username = "***"
            password = "***"
        }
        mUserCredential = UserCredential(username, password)
        setLoadSettings(arcGISMap)
        createMapBox()

        mapView.map = arcGISMap
//        initLocationDisplay()
        // ?????????????????????
        mapView.onTouchListener = DefaultMapViewNoRotateOnTouchListener(mContext, mapView)
        // ?????????????????????
        mapView.isAttributionTextVisible = false
        mapView.map.maxScale = mMaxScale
        mapView.map.addDoneLoadingListener {
            if (arcGISMap.loadStatus == LoadStatus.LOADED) { // ????????????
                mGraphicsOverlay = GraphicsOverlay()
                mapView.graphicsOverlays.add(mGraphicsOverlay)
                initLocation()
                if (mLoadMapListener != null) {
                    mLoadMapListener!!.onLoadComplete()
                }
            } else if (arcGISMap.loadStatus == LoadStatus.LOADING) { // ?????????
                if (mLoadMapListener != null) {
                    mLoadMapListener!!.onLoading()
                }
            } else { // ????????????
                if (mLoadMapListener != null) {
                    mLoadMapListener!!.onFailed(
                        arcGISMap.loadStatus.ordinal, arcGISMap.loadError.message
                    )
                }
            }
        }
    }

    /**
     * ?????????????????????
     */
    private fun initLocation() {
        mLocationOperations = LocationOperations(context.applicationContext)
        // ??????????????????
        mLocationOperations!!.setInterval(3000)
        mLocationOperations!!.setTransform(TransformGcj02ToWgs84())
        mLocationOperations!!.setLocationListener(object : ILocationListener {
            override fun onLocationSuccess(location: ILocation) {
                mLocationOperations!!.stop()
                mCurPoint = Point(location.longitude, location.latitude)
                addMarker()
                moveToLocation()
                if (mLocationListener != null) {
                    mLocationListener?.onLocationSuccess(location)
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
     * ??????????????? marker
     */
    private fun addMarker() {
        mGraphicsOverlay!!.graphics.clear()
        val marker = ContextCompat.getDrawable(
                context.applicationContext,
                R.mipmap.current_location
            ) as BitmapDrawable?
        val async = PictureMarkerSymbol.createAsync(marker)
        async.addDoneListener {
            try {
                async.get().height = 44f
                async.get().width = 44f
                val graphic = Graphic(mCurPoint, async.get())
                mGraphicsOverlay?.graphics?.add(graphic)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * ?????????????????????
     */
    private fun moveToLocation() {
        mapView.setViewpointCenterAsync(mCurPoint, 5.0)
    }


    private fun createMapBox() {
        setLoadSettings(arcGISMap)
        val createBaseMap =
            MapBoxCacheTiledLayer.createBaseMap("https://api.mapbox.com/styles/v1/tanqidighj/cl3e5idki000t15o4b4wixidz/tiles/256/{level}/{col}/{row}@2x?access_token=pk.eyJ1IjoidGFucWlkaWdoaiIsImEiOiJjbDNlNWFqb2cwY3BrM2dxa3FyZGw2NWExIn0.9yZR9yKu2oi4_8x0aYMR5w")
        arcGISMap.basemap = createBaseMap
        mapView.map = arcGISMap
    }

    private fun createTdt() {
        val webTiledLayer = TianDiTuTiledLayer.CreateTianDiTuTiledLayer(
            TianDiTuTiledLayer.LayerType.TIANDITU_IMAGE_MERCATOR, "tianditu"
        )
        //????????????
        val webTiledLayerLable = TianDiTuTiledLayer.CreateTianDiTuTiledLayer(
            TianDiTuTiledLayer.LayerType.TIANDITU_IMAGE_MERCATOR_LABLE, "tianditu_lable"
        )
        val basemap = Basemap()
        basemap.baseLayers.add(webTiledLayer)
        basemap.baseLayers.add(webTiledLayerLable)
        arcGISMap.basemap = basemap
        mapView.map = arcGISMap
    }


    /**
     * ?????????????????? layer
     */
    @Synchronized
    private fun initTdtLayer() {
        val layerList = mapView.map.operationalLayers ?: return
        createTdtImageLayer()
        createTdtImageLabelLayer()
        createTdtVectorLayer()
        createTdtVectorLabelLayer()
        try {
            if (!layerList.contains(mImageLayer)) {
                layerList.add(mImageLayer)
            }
            if (!layerList.contains(mImageLabelLayer)) {
                layerList.add(mImageLabelLayer)
            }
            if (!layerList.contains(mVectorLayer)) {
                layerList.add(mVectorLayer)
            }
            if (!layerList.contains(mVectorLabelLayer)) {
                layerList.add(mVectorLabelLayer)
            }
            switchImage()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * ??????????????? ?????? label layer
     */
    private fun createTdtVectorLabelLayer() {
        if (mVectorLabelLayer != null) {
            return
        }
        mVectorLabelLayer =
            CreateTdtTileLayer().createTianDiTuTiledLayer(TdtLayerType.TIANDITU_VECTOR_ANNOTATION_CHINESE_2000)
        mVectorLabelLayer!!.id = TDT_VECTOR_LAYER_LABEL_ID
        mVectorLabelLayer!!.maxScale = mMaxScale
    }

    /**
     * ??????????????? ?????? layer
     */
    private fun createTdtVectorLayer() {
        if (mVectorLayer != null) {
            return
        }
        mVectorLayer =
            CreateTdtTileLayer().createTianDiTuTiledLayer(TdtLayerType.TIANDITU_VECTOR_2000)
        mVectorLayer!!.id = TDT_VECTOR_LAYER_ID
        mVectorLayer!!.maxScale = mMaxScale
    }

    /**
     * ??????????????? ??????label layer
     */
    private fun createTdtImageLabelLayer() {
        if (mImageLabelLayer != null) {
            return
        }
        mImageLabelLayer =
            CreateTdtTileLayer().createTianDiTuTiledLayer(TdtLayerType.TIANDITU_IMAGE_ANNOTATION_CHINESE_2000)
        mImageLabelLayer!!.maxScale = mMaxScale
        mImageLabelLayer!!.id = TDT_IMAGE_LAYER_LABEL_ID
    }

    /**
     * ??????????????? ??????layer
     */
    private fun createTdtImageLayer() {
        if (mImageLayer != null) {
            return
        }
        mImageLayer =
            CreateTdtTileLayer().createTianDiTuTiledLayer(TdtLayerType.TIANDITU_IMAGE_2000)
        mImageLayer!!.maxScale = mMaxScale
        mImageLayer!!.id = TDT_IMAGE_LAYER_ID
    }

    /**
     * ????????????????????????
     *
     * @param listener listener
     */
    fun setOnLoadMapListener(listener: LoadMapListener?) {
        mLoadMapListener = listener
    }

    /**
     * ????????????
     */
    fun changeImageLayer() {
        isShowVector = !isShowVector
        switchImage()
    }

    /**
     * ????????????
     */
    private fun switchImage() {
        if (!isShowVector) {
            createTdt()
        } else {
            createMapBox()
        }
//        mImageLabelLayer!!.isVisible = !isShowVector
//        mImageLayer!!.isVisible = !isShowVector
//        mVectorLabelLayer!!.isVisible = isShowVector
//        mVectorLayer!!.isVisible = isShowVector
    }

    //?????????????????????????????????DYNAMIC???????????????STATIC
    private fun setLoadSettings(arcGISMap: ArcGISMap) {
        val loadSettings = LoadSettings()
        loadSettings.preferredPointFeatureRenderingMode = FeatureLayer.RenderingMode.DYNAMIC
        loadSettings.preferredPolylineFeatureRenderingMode = FeatureLayer.RenderingMode.DYNAMIC
        loadSettings.preferredPolygonFeatureRenderingMode = FeatureLayer.RenderingMode.DYNAMIC
        arcGISMap.loadSettings = loadSettings
    }

    /**
     * ???????????????????????????
     */
    fun initLocationDisplay() {
        val locationDisplay = mapView.locationDisplay
        locationDisplay?.isShowLocation = true
        locationDisplay?.isShowPingAnimation = true
        locationDisplay?.isShowAccuracy = true
        locationDisplay?.autoPanMode = LocationDisplay.AutoPanMode.RECENTER
        locationDisplay?.locationDataSource = AndroidLocationDataSource(mContext)
        if (!locationDisplay?.isStarted!!) locationDisplay.startAsync()
    }

    /**
     * ??????????????????
     *
     * @param listener listener
     */
    fun setLocationListener(listener: ILocationListener?) {
        mLocationListener = listener
    }

    /**
     * ????????????
     */
    fun startLocation() {
        mLocationOperations?.start()
    }

    fun onPause() {
        mapView.pause()
    }

    fun onResume() {
        mapView.resume()
    }

    fun onDestroy() {
        if (mLocationOperations != null) {
            mLocationOperations?.destroy()
        }
        mapView.dispose()
    }

    /**
     * ????????????????????????
     *
     * @return ??????????????????
     */
    val center: com.esri.arcgisruntime.geometry.Point
        get() {
            val centerPoint = Point(
                (mapView.left + mapView.width) / 2, (mapView.top + mapView.height) / 2
            )
            val point = mapView.screenToLocation(centerPoint)
            return Point(point.x, point.y)
        }

    /**
     * ????????????????????????
     *
     * @param viewpoint ???????????????
     */
    fun setCenter(viewpoint: Viewpoint?) {
        mapView.setViewpointAsync(viewpoint, 0f)
    }

    companion object {
        private const val TAG = "BaseMapView"
        private const val TDT_IMAGE_LAYER_ID = "image_layer_id"
        private const val TDT_VECTOR_LAYER_ID = "vector_layer_id"
        const val TDT_IMAGE_LAYER_LABEL_ID = "image_label_layer_id"
        const val TDT_VECTOR_LAYER_LABEL_ID = "vector_label_layerId"
    }
}