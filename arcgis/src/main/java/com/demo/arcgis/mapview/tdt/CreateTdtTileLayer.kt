/*
 * Copyright (C) 2018 Beijing GAGO Technology Ltd.
 */
package com.demo.arcgis.mapview.tdt

import com.esri.arcgisruntime.layers.WebTiledLayer
import com.esri.arcgisruntime.arcgisservices.TileInfo
import com.esri.arcgisruntime.arcgisservices.LevelOfDetail
import com.esri.arcgisruntime.layers.WmtsLayer
import com.esri.arcgisruntime.geometry.*
import java.lang.Exception
import java.util.*

/**
 * @author guoyalong
 * @desc 创建天地图的layer
 */
class CreateTdtTileLayer {
    /**
     * 创建layer
     *
     * @param layerType layerType
     * @return WebTiledLayer
     */
    fun createTianDiTuTiledLayer(layerType: TdtLayerType?): WebTiledLayer? {
        var webTiledLayer: WebTiledLayer? = null
        var mainUrl = ""
        var mainName = ""
        var mainTileInfo: TileInfo? = null
        var mainEnvelope: Envelope? = null
        var mainIs2000 = false
        try {
            when (layerType) {
                TdtLayerType.TIANDITU_VECTOR_2000 -> {
                    mainUrl = URL_VECTOR_2000
                    mainName = LAYER_NAME_VECTOR
                    mainIs2000 = true
                }
                TdtLayerType.TIANDITU_VECTOR_MERCATOR -> {
                    mainUrl = URL_VECTOR_MERCATOR
                    mainName = LAYER_NAME_VECTOR
                }
                TdtLayerType.TIANDITU_IMAGE_2000 -> {
                    mainUrl = URL_IMAGE_2000
                    mainName = LAYER_NAME_IMAGE
                    mainIs2000 = true
                }
                TdtLayerType.TIANDITU_IMAGE_ANNOTATION_CHINESE_2000 -> {
                    mainUrl = URL_IMAGE_ANNOTATION_CHINESE_2000
                    mainName = LAYER_NAME_IMAGE_ANNOTATION_CHINESE
                    mainIs2000 = true
                }
                TdtLayerType.TIANDITU_IMAGE_ANNOTATION_ENGLISH_2000 -> {
                    mainUrl = URL_IMAGE_ANNOTATION_ENGLISH_2000
                    mainName = LAYER_NAME_IMAGE_ANNOTATION_ENGLISH
                    mainIs2000 = true
                }
                TdtLayerType.TIANDITU_IMAGE_ANNOTATION_CHINESE_MERCATOR -> {
                    mainUrl = URL_IMAGE_ANNOTATION_CHINESE_MERCATOR
                    mainName = LAYER_NAME_IMAGE_ANNOTATION_CHINESE
                }
                TdtLayerType.TIANDITU_IMAGE_ANNOTATION_ENGLISH_MERCATOR -> {
                    mainUrl = URL_IMAGE_ANNOTATION_ENGLISH_MERCATOR
                    mainName = LAYER_NAME_IMAGE_ANNOTATION_ENGLISH
                }
                TdtLayerType.TIANDITU_IMAGE_MERCATOR -> {
                    mainUrl = URL_IMAGE_MERCATOR
                    mainName = LAYER_NAME_IMAGE
                }
                TdtLayerType.TIANDITU_VECTOR_ANNOTATION_CHINESE_2000 -> {
                    mainUrl = URL_VECTOR_ANNOTATION_CHINESE_2000
                    mainName = LAYER_NAME_VECTOR_ANNOTATION_CHINESE
                    mainIs2000 = true
                }
                TdtLayerType.TIANDITU_VECTOR_ANNOTATION_ENGLISH_2000 -> {
                    mainUrl = URL_VECTOR_ANNOTATION_ENGLISH_2000
                    mainName = LAYER_NAME_VECTOR_ANNOTATION_ENGLISH
                    mainIs2000 = true
                }
                TdtLayerType.TIANDITU_VECTOR_ANNOTATION_CHINESE_MERCATOR -> {
                    mainUrl = URL_VECTOR_ANNOTATION_CHINESE_MERCATOR
                    mainName = LAYER_NAME_VECTOR_ANNOTATION_CHINESE
                }
                TdtLayerType.TIANDITU_VECTOR_ANNOTATION_ENGLISH_MERCATOR -> {
                    mainUrl = URL_VECTOR_ANNOTATION_ENGLISH_MERCATOR
                    mainName = LAYER_NAME_VECTOR_ANNOTATION_ENGLISH
                }
                TdtLayerType.TIANDITU_TERRAIN_2000 -> {
                    mainUrl = URL_TERRAIN_2000
                    mainName = LAYER_NAME_TERRAIN
                    mainIs2000 = true
                }
                TdtLayerType.TIANDITU_TERRAIN_ANNOTATION_CHINESE_2000 -> {
                    mainUrl = URL_TERRAIN_ANNOTATION_CHINESE_2000
                    mainName = LAYER_NAME_TERRAIN_ANNOTATION_CHINESE
                    mainIs2000 = true
                }
                TdtLayerType.TIANDITU_TERRAIN_MERCATOR -> {
                    mainUrl = URL_TERRAIN_MERCATOR
                    mainName = LAYER_NAME_TERRAIN
                }
                TdtLayerType.TIANDITU_TERRAIN_ANNOTATION_CHINESE_MERCATOR -> {
                    mainUrl = URL_TERRAIN_ANNOTATION_CHINESE_MERCATOR
                    mainName = LAYER_NAME_TERRAIN_ANNOTATION_CHINESE
                }
                else -> {}
            }
            val mainLevelOfDetail: MutableList<LevelOfDetail> = ArrayList()
            var mainOrigin: Point? = null
            if (mainIs2000 == true) {
                for (i in MINZOOMLEVEL..MAXZOOMLEVEL) {
                    val item = LevelOfDetail(
                        i, RESOLUTIONS_2000[i - 1],
                        SCALES[i - 1]
                    )
                    mainLevelOfDetail.add(item)
                }
                mainEnvelope = ENVELOPE_2000
                mainOrigin = ORIGIN_2000
            } else {
                for (i in MINZOOMLEVEL..MAXZOOMLEVEL) {
                    val item = LevelOfDetail(
                        i, RESOLUTIONS_MERCATOR[i - 1],
                        SCALES[i - 1]
                    )
                    mainLevelOfDetail.add(item)
                }
                mainEnvelope = ENVELOPE_MERCATOR
                mainOrigin = ORIGIN_MERCATOR
            }
            mainTileInfo = TileInfo(
                DPI,
                TileInfo.ImageFormat.PNG24,
                mainLevelOfDetail,
                mainOrigin,
                mainOrigin.spatialReference,
                TITLEHEIGHT,
                TITLEWIDTH
            )
            webTiledLayer = WebTiledLayer(
                mainUrl,
                SUBDOMAIN,
                mainTileInfo,
                mainEnvelope
            )
            webTiledLayer.name = mainName
            webTiledLayer.loadAsync()
        } catch (e: Exception) {
            e.cause
        }
        return webTiledLayer
    }

    fun createTianDiTuTiledLayer1(layerType: TdtLayerType?): WebTiledLayer? {
        var webTiledLayer: WebTiledLayer? = null
        var mainUrl = ""
        var mainName = ""
        var mainTileInfo: TileInfo? = null
        var mainEnvelope: Envelope? = null
        var mainIs2000 = false
        try {
            when (layerType) {
                TdtLayerType.HD_ANNOTATION_CHINESE_2000 -> {
                    mainUrl = URL_IMAGE_HS
                    mainName = LAYER_NAME_IMAGE
                    mainIs2000 = true
                }
                else -> {}
            }
            val mainLevelOfDetail: MutableList<LevelOfDetail> = ArrayList()
            var mainOrigin: Point? = null
            if (mainIs2000 == true) {
                for (i in MINZOOMLEVEL..MAXZOOMLEVEL) {
                    val item = LevelOfDetail(
                        i, RESOLUTIONS_2000[i - 1],
                        SCALES[i - 1]
                    )
                    mainLevelOfDetail.add(item)
                }
                mainEnvelope = ENVELOPE_2000
                mainOrigin = ORIGIN_2000
            } else {
                for (i in MINZOOMLEVEL..MAXZOOMLEVEL) {
                    val item = LevelOfDetail(
                        i, RESOLUTIONS_MERCATOR[i - 1],
                        SCALES[i - 1]
                    )
                    mainLevelOfDetail.add(item)
                }
                mainEnvelope = ENVELOPE_MERCATOR
                mainOrigin = ORIGIN_MERCATOR
            }
            mainTileInfo = TileInfo(
                DPI,
                TileInfo.ImageFormat.PNG24,
                mainLevelOfDetail,
                mainOrigin,
                mainOrigin.spatialReference,
                TITLEHEIGHT,
                TITLEWIDTH
            )
            webTiledLayer = WebTiledLayer(
                mainUrl,
                Arrays.asList(*arrayOf("services")),
                mainTileInfo,
                mainEnvelope
            )
            webTiledLayer.name = mainName
            webTiledLayer.loadAsync()
        } catch (e: Exception) {
            e.cause
        }
        return webTiledLayer
    }

    companion object {
        private val SUBDOMAIN = Arrays.asList("t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7")

        // 天地图的key
        private const val TDT_KEY = "093f7ad38d9ee1d68221be93df448a2d"
        private const val URL_VECTOR_2000 =
            "http://{subDomain}.tianditu.gov.cn/vec_c/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=vec&tileMatrixSet=c&TileMatrix={level}&TileRow={row}&TileCol={col}&style=default&format=tiles&tk=" + TDT_KEY
        private const val URL_VECTOR_ANNOTATION_CHINESE_2000 =
            "http://{subDomain}.tianditu.gov.cn/cva_c/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=cva&tileMatrixSet=c&TileMatrix={level}&TileRow={row}&TileCol={col}&style=default&format=tiles&tk=" + TDT_KEY
        private const val URL_VECTOR_ANNOTATION_ENGLISH_2000 =
            "http://{subDomain}.tianditu.gov.cn/eva_c/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=eva&tileMatrixSet=c&TileMatrix={level}&TileRow={row}&TileCol={col}&style=default&format=tiles&tk=" + TDT_KEY
        private const val URL_IMAGE_2000 =
            "http://{subDomain}.tianditu.gov.cn/img_c/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=img&tileMatrixSet=c&TileMatrix={level}&TileRow={row}&TileCol={col}&style=default&format=tiles&tk=" + TDT_KEY
        private const val URL_IMAGE_HS =
            "http://{subDomain}.tianditugd.com/DOM_GF_GK2_2014/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=DOM_GF_GK2_2014&TileMatrixSet=Matrix_0&TileMatrix={level}&TileRow={row}&TileCol={col}&style=DOM_GF_GK2_2014&format=image/png"

        //    private static final String URL_IMAGE_2000 = "http://mt3.google.cn/vt/lyrs=y&hl=zh-CN&gl=cn&scale=4&x={col}&y={row}&z={level}";
        private const val URL_IMAGE_ANNOTATION_CHINESE_2000 =
            "http://{subDomain}.tianditu.gov.cn/cia_c/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=cia&tileMatrixSet=c&TileMatrix={level}&TileRow={row}&TileCol={col}&style=default&format=tiles&tk=" + TDT_KEY
        private const val URL_IMAGE_ANNOTATION_ENGLISH_2000 =
            "http://{subDomain}.tianditu.gov.cn/eia_c/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=eia&tileMatrixSet=c&TileMatrix={level}&TileRow={row}&TileCol={col}&style=default&format=tiles&tk=" + TDT_KEY
        private const val URL_TERRAIN_2000 =
            "http://{subDomain}.tianditu.gov.cn/ter_c/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=ter&tileMatrixSet=c&TileMatrix={level}&TileRow={row}&TileCol={col}&style=default&format=tiles&tk=" + TDT_KEY
        private const val URL_TERRAIN_ANNOTATION_CHINESE_2000 =
            "http://{subDomain}.tianditu.gov.cn/cta_c/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=cta&tileMatrixSet=c&TileMatrix={level}&TileRow={row}&TileCol={col}&style=default&format=tiles&tk=" + TDT_KEY
        private const val URL_VECTOR_MERCATOR =
            "http://{subDomain}.tianditu.gov.cn/DataServer?T=vec_w&x={col}&y={row}&l={level}&tk=" + TDT_KEY
        private const val URL_VECTOR_ANNOTATION_CHINESE_MERCATOR =
            "http://{subDomain}.tianditu.gov.cn/DataServer?T=cva_w&x={col}&y={row}&l={level}&tk=" + TDT_KEY
        private const val URL_VECTOR_ANNOTATION_ENGLISH_MERCATOR =
            "http://{subDomain}.tianditu.gov.cn/DataServer?T=eva_w&x={col}&y={row}&l={level}&tk=" + TDT_KEY
        private const val URL_IMAGE_MERCATOR =
            "http://{subDomain}.tianditu.gov.cn/DataServer?T=img_w&x={col}&y={row}&l={level}&tk=" + TDT_KEY
        private const val URL_IMAGE_ANNOTATION_CHINESE_MERCATOR =
            "http://{subDomain}.tianditu.gov.cn/DataServer?T=cia_w&x={col}&y={row}&l={level}&tk=" + TDT_KEY
        private const val URL_IMAGE_ANNOTATION_ENGLISH_MERCATOR =
            "http://{subDomain}.tianditu.gov.cn/DataServer?T=eia_w&x={col}&y={row}&l={level}&tk=" + TDT_KEY
        private const val URL_TERRAIN_MERCATOR =
            "http://{subDomain}.tianditu.gov.cn/DataServer?T=ter_w&x={col}&y={row}&l={level}&tk=" + TDT_KEY
        private const val URL_TERRAIN_ANNOTATION_CHINESE_MERCATOR =
            "http://{subDomain}.tianditu.gov.cn/DataServer?T=cta_w&x={col}&y={row}&l={level}&tk=" + TDT_KEY
        private const val DPI = 96
        private const val MINZOOMLEVEL = 1
        private const val MAXZOOMLEVEL = 18
        private const val TITLEWIDTH = 256
        private const val TITLEHEIGHT = 256
        private const val LAYER_NAME_VECTOR = "vec"
        private const val LAYER_NAME_VECTOR_ANNOTATION_CHINESE = "cva"
        private const val LAYER_NAME_VECTOR_ANNOTATION_ENGLISH = "eva"
        private const val LAYER_NAME_IMAGE = "img"
        private const val LAYER_NAME_IMAGE_ANNOTATION_CHINESE = "cia"
        private const val LAYER_NAME_IMAGE_ANNOTATION_ENGLISH = "eia"
        private const val LAYER_NAME_TERRAIN = "ter"
        private const val LAYER_NAME_TERRAIN_ANNOTATION_CHINESE = "cta"
        private val SRID_2000 = SpatialReference.create(4490)
        private val SRID_MERCATOR = SpatialReference.create(102100)
        private const val X_MIN_2000 = -180.0
        private const val Y_MIN_2000 = -90.0
        private const val X_MAX_2000 = 180.0
        private const val Y_MAX_2000 = 90.0
        private const val X_MIN_MERCATOR = -20037508.3427892
        private const val Y_MIN_MERCATOR = -20037508.3427892
        private const val X_MAX_MERCATOR = 20037508.3427892
        private const val Y_MAX_MERCATOR = 20037508.3427892
        private val ORIGIN_2000: Point = Point(-180.0, 90.0, SRID_2000)
        private val ORIGIN_MERCATOR = Point(
            -20037508.3427892,
            20037508.3427892, SRID_MERCATOR
        )
        private val ENVELOPE_2000 = Envelope(
            X_MIN_2000, Y_MIN_2000, X_MAX_2000,
            Y_MAX_2000, SRID_2000
        )
        private val ENVELOPE_MERCATOR = Envelope(
            X_MIN_MERCATOR, Y_MIN_MERCATOR,
            X_MAX_MERCATOR, Y_MAX_MERCATOR, SRID_MERCATOR
        )
        private val SCALES = doubleArrayOf(
            2.958293554545656E8, 1.479146777272828E8,
            7.39573388636414E7, 3.69786694318207E7,
            1.848933471591035E7, 9244667.357955175,
            4622333.678977588, 2311166.839488794,
            1155583.419744397, 577791.7098721985,
            288895.85493609926, 144447.92746804963,
            72223.96373402482, 36111.98186701241,
            18055.990933506204, 9027.995466753102,
            4513.997733376551, 2256.998866688275,
            1128.4994333441375, 564.249716672
        )
        private val RESOLUTIONS_MERCATOR = doubleArrayOf(
            78271.51696402048, 39135.75848201024,
            19567.87924100512, 9783.93962050256,
            4891.96981025128, 2445.98490512564,
            1222.99245256282, 611.49622628141,
            305.748113140705, 152.8740565703525,
            76.43702828517625, 38.21851414258813,
            19.109257071294063, 9.554628535647032,
            4.777314267823516, 2.388657133911758,
            1.194328566955879, 0.5971642834779395,
            0.298582141738970, 0.14929107086
        )
        private val RESOLUTIONS_2000 = doubleArrayOf(
            0.7031249999891485, 0.35156249999999994,
            0.17578124999999997, 0.08789062500000014,
            0.04394531250000007, 0.021972656250000007,
            0.01098632812500002, 0.00549316406250001,
            0.0027465820312500017, 0.0013732910156250009,
            0.000686645507812499, 0.0003433227539062495,
            0.00017166137695312503, 0.00008583068847656251,
            0.000042915344238281406, 0.000021457672119140645,
            0.000010728836059570307, 0.000005364418029785169
        )

        fun createWmtsLayer(): WmtsLayer {
            return WmtsLayer(URL_IMAGE_2000, "wmts_layer")
        }

        fun createHsLayer(): WmtsLayer {
            return WmtsLayer(
                "http://services.tianditugd.com/DOM_GF_GK2_2014/wmts?SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetCapabilities",
                "ssss"
            )
        }

        /**
         * 根据zoom获取范围
         *
         * @param level zoom
         * @return double
         */
        fun getScaleByLevel(level: Int): Double {
            return if (level < 0) {
                SCALES[0]
            } else if (level > SCALES.size - 1) {
                SCALES[SCALES.size - 1]
            } else {
                SCALES[level]
            }
        }

        /**
         * 根据scale获取zoom
         *
         * @param scale scale
         * @return zoom 默认返回0
         */
        fun getLevelByScale(scale: Double): Int {
            for (i in 0 until SCALES.size - 1) {
                if (scale > SCALES[0]) {
                    return 0
                }
                if (scale < SCALES[SCALES.size - 1]) {
                    return SCALES.size - 1
                }
                if (SCALES[i] < scale && scale >= SCALES[i + 1]) {
                    return i
                }
            }
            return 0
        }

        /**
         * 根据屏幕大小获取缩放等级
         *
         * @param scale 屏幕大小
         * @return zoom
         */
        fun getZoomByScale(scale: Double): Double {
            if (scale >= SCALES[0]) {
                return 0.0
            }
            return if (scale <= SCALES[SCALES.size - 1]) {
                19.0
            } else 19 - Math.log(
                scale / SCALES[SCALES.size - 1]
            ) / Math.log(2.0)
        }

        /**
         * 根据缩放等级获取屏幕大小
         *
         * @param zoom zoom
         * @return scale
         */
        fun getScaleByZoom(zoom: Double): Double {
            if (zoom <= 0) {
                return SCALES[0]
            }
            return if (zoom >= 19) {
                SCALES[SCALES.size - 1]
            } else SCALES[SCALES.size - 1] * Math.pow(
                2.0,
                19 - zoom
            )
        }
    }
}