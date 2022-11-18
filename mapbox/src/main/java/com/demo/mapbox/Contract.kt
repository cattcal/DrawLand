package com.demo.mapbox

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
 */
object Contract {
    /**
     * 空的layer，别的layer 在其上面，底图部分都在其下面
     */
    const val BASE_EMPTY_LAYER = "base_empty_layer"

    // 天地图的key
    private const val TDT_KEY = "093f7ad38d9ee1d68221be93df448a2d"
    private const val XINGTU_TOKEN =
        "d6589e2acc19ff52e50ec4661ad2377e2d447a19218441e9f0e96edf39ba57d2"

    // --------- 天地图地图的url ---------
    // 电子地图url
    const val DIGITAL_SOURCE_URL =
        "https://t0.tianditu.gov.cn/DataServer?T=vec_w&x={x}&y={y}&l={z}&tk=$TDT_KEY"

    // 卫星地图url
    const val SATELLITE_SOURCE_URL =
        "https://t0.tianditu.gov.cn/img_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=img&tileMatrixSet=w&TileMatrix={z}&TileRow={y}&TileCol={x}&style=default&format=tiles&tk=$TDT_KEY"

    // 路网地图url
    const val ROAD_SOURCE_URL =
        "https://t0.tianditu.gov.cn/DataServer?T=cia_w&x={x}&y={y}&l={z}&tk=$TDT_KEY"

    // 注记url
    const val ZHUJI_SOURCE_URL =
        "https://t0.tianditu.gov.cn/DataServer?T=cva_w&x={x}&y={y}&l={z}&tk=$TDT_KEY"

    //星图卫星底图
    const val XINGTU_IMG_SOURCE_URL =
        "https://tiles1.geovisearth.com/base/v1/img/{z}/{x}/{y}?format=webp&tmsIds=w&token=${XINGTU_TOKEN}"
    //星图标注
    const val XINGTU_CIA_SOURCE_URL =
        "https://tiles1.geovisearth.com/base/v1/cia/{z}/{x}/{y}?format=png&tmsIds=w&token=${XINGTU_TOKEN}"
}