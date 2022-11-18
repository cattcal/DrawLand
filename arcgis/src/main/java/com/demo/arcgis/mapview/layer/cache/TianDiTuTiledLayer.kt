package com.demo.arcgis.mapview.layer.cache

import com.esri.arcgisruntime.arcgisservices.LevelOfDetail
import com.esri.arcgisruntime.arcgisservices.TileInfo
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.layers.ImageTiledLayer
import java.util.Arrays

object TianDiTuTiledLayer {
    //需要自己去申请,绑定包名和SHA1，我这个你们用不了
    private val SubDomain = Arrays.asList(*arrayOf("t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7"))
    private const val DPI = 96
    private const val minZoomLevel = 1
    private const val maxZoomLevel = 18
    private const val tileWidth = 256
    private const val tileHeight = 256
    private val SRID_MERCATOR = SpatialReference.create(102100)
    private const val X_MIN_MERCATOR = -20037508.3427892
    private const val Y_MIN_MERCATOR = -20037508.3427892
    private const val X_MAX_MERCATOR = 20037508.3427892
    private const val Y_MAX_MERCATOR = 20037508.3427892
    private val ORIGIN_MERCATOR = Point(-20037508.3427892, 20037508.3427892, SRID_MERCATOR)
    private val ENVELOPE_MERCATOR = Envelope(
        X_MIN_MERCATOR,
        Y_MIN_MERCATOR,
        X_MAX_MERCATOR,
        Y_MAX_MERCATOR,
        SRID_MERCATOR
    )
    private val SRID_2000 = SpatialReference.create(4490)
    private const val X_MIN_2000 = -180.0
    private const val Y_MIN_2000 = -90.0
    private const val X_MAX_2000 = 180.0
    private const val Y_MAX_2000 = 90.0
    private val ORIGIN_2000 = Point(-180.0, 90.0, SRID_2000)
    private val ENVELOPE_2000 = Envelope(
        X_MIN_2000,
        Y_MIN_2000,
        X_MAX_2000,
        Y_MAX_2000,
        SRID_2000
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
        1128.4994333441375
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
        0.298582141738970
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

    fun CreateTianDiTuTiledLayer(layerType: LayerType?, tileType: String?): ImageTiledLayer? {
        var type = ""
        var tilematrixset = ""
        var webTiledLayer: ImageTiledLayer? = null
        val mainUrl: String
        val mainTileInfo: TileInfo
        val mainEnvelope: Envelope
        try {
            when (layerType) {
                LayerType.TIANDITU_VECTOR_MERCATOR -> {
                    type = "vec"
                    tilematrixset = "w"
                }

                LayerType.TIANDITU_VECTOR_MERCATOR_LABLE -> {
                    type = "cva"
                    tilematrixset = "w"
                }

                LayerType.TIANDITU_VECTOR_2000 -> {
                    type = "vec"
                    tilematrixset = "c"
                }

                LayerType.TIANDITU_VECTOR_2000_LABLE -> {
                    type = "cva"
                    tilematrixset = "c"
                }

                LayerType.TIANDITU_IMAGE_MERCATOR -> {
                    type = "img"
                    tilematrixset = "w"
                }

                LayerType.TIANDITU_IMAGE_MERCATOR_LABLE -> {
                    type = "cia"
                    tilematrixset = "w"
                }

                LayerType.TIANDITU_IMAGE_2000 -> {
                    type = "img"
                    tilematrixset = "c"
                }

                LayerType.TIANDITU_IMAGE_2000_LABLE -> {
                    type = "cia"
                    tilematrixset = "c"
                }

                LayerType.TIANDITU_TERRAIN_MERCATOR -> {
                    type = "ter"
                    tilematrixset = "w"
                }

                LayerType.TIANDITU_TERRAIN_MERCATOR_LABLE -> {
                    type = "cta"
                    tilematrixset = "w"
                }

                LayerType.TIANDITU_TERRAIN_2000 -> {
                    type = "ter"
                    tilematrixset = "c"
                }

                LayerType.TIANDITU_TERRAIN_2000_LABLE -> {
                    type = "cta"
                    tilematrixset = "c"
                }

                else -> {}
            }
            mainUrl = ("http://t0.tianditu.gov.cn/"
                    + type + "_" + tilematrixset + "/wmts?" +
                    "service=wmts" +
                    "&request=gettile" +
                    "&version=1.0.0" +
                    "&layer=" + type +
                    "&format=tiles" +
                    "&STYLE=default" +
                    "&tilematrixset=" + tilematrixset +
                    "&tilecol={col}" +
                    "&tilerow={row}" +
                    "&tilematrix={level}" +
                    "&tk=84a2c203c8ed6a78c67bfac56209fdfb")
            val mainLevelOfDetail: MutableList<LevelOfDetail> = ArrayList()
            val mainOrigin: Point
            if (tilematrixset == "c") {
                for (i in minZoomLevel..maxZoomLevel) {
                    val item = LevelOfDetail(
                        i,
                        RESOLUTIONS_2000[i - 1], SCALES[i - 1]
                    )
                    mainLevelOfDetail.add(item)
                }
                mainEnvelope = ENVELOPE_2000
                mainOrigin = ORIGIN_2000
            } else {
                for (i in minZoomLevel..maxZoomLevel) {
                    val item = LevelOfDetail(
                        i,
                        RESOLUTIONS_MERCATOR[i - 1], SCALES[i - 1]
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
                tileHeight,
                tileWidth
            )
            webTiledLayer = BaseTileCacheLayer(
                mainUrl, mainTileInfo, mainEnvelope, "天地图地图缓存",
                tileType!!
            )
            webTiledLayer.setName(type)
            webTiledLayer.loadAsync()
        } catch (e: Exception) {
            e.cause
        }
        return webTiledLayer
    }

    enum class LayerType {
        /**
         * 天地图矢量墨卡托投影地图服务
         */
        TIANDITU_VECTOR_MERCATOR, TIANDITU_VECTOR_MERCATOR_LABLE,

        /**
         * 天地图矢量2000地图服务
         */
        TIANDITU_VECTOR_2000, TIANDITU_VECTOR_2000_LABLE,

        /**
         * 天地图影像墨卡托地图服务
         */
        TIANDITU_IMAGE_MERCATOR, TIANDITU_IMAGE_MERCATOR_LABLE,

        /**
         * 天地图影像2000地图服务
         */
        TIANDITU_IMAGE_2000, TIANDITU_IMAGE_2000_LABLE,

        /**
         * 天地图地形墨卡托地图服务
         */
        TIANDITU_TERRAIN_MERCATOR, TIANDITU_TERRAIN_MERCATOR_LABLE,

        /**
         * 天地图地形2000地图服务
         */
        TIANDITU_TERRAIN_2000, TIANDITU_TERRAIN_2000_LABLE
    }
}
