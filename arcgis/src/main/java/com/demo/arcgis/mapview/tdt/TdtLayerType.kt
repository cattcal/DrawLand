package com.demo.arcgis.mapview.tdt

/**
 * @author guoyalong
 * @desc 天地图的一些类型
 */
enum class TdtLayerType {
    /**
     * 天地图矢量墨卡托投影地图服务
     */
    TIANDITU_VECTOR_MERCATOR,

    /**
     * 天地图矢量墨卡托中文标注
     */
    TIANDITU_VECTOR_ANNOTATION_CHINESE_MERCATOR,

    /**
     * 天地图矢量墨卡托英文标注
     */
    TIANDITU_VECTOR_ANNOTATION_ENGLISH_MERCATOR,

    /**
     * 天地图影像墨卡托投影地图服务
     */
    TIANDITU_IMAGE_MERCATOR,

    /**
     * 天地图影像墨卡托投影中文标注
     */
    TIANDITU_IMAGE_ANNOTATION_CHINESE_MERCATOR,

    /**
     * 天地图影像墨卡托投影英文标注
     */
    TIANDITU_IMAGE_ANNOTATION_ENGLISH_MERCATOR,

    /**
     * 天地图地形墨卡托投影地图服务
     */
    TIANDITU_TERRAIN_MERCATOR,

    /**
     * 天地图地形墨卡托投影中文标注
     */
    TIANDITU_TERRAIN_ANNOTATION_CHINESE_MERCATOR,

    /**
     * 天地图矢量国家2000坐标系地图服务
     */
    TIANDITU_VECTOR_2000,

    /**
     * 天地图矢量国家2000坐标系中文标注
     */
    TIANDITU_VECTOR_ANNOTATION_CHINESE_2000,

    /**
     * 天地图矢量国家2000坐标系英文标注
     */
    TIANDITU_VECTOR_ANNOTATION_ENGLISH_2000,

    /**
     * 天地图影像国家2000坐标系地图服务
     */
    TIANDITU_IMAGE_2000,

    /**
     * 天地图影像国家2000坐标系中文标注
     */
    TIANDITU_IMAGE_ANNOTATION_CHINESE_2000,

    /**
     * 天地图影像国家2000坐标系英文标注
     */
    TIANDITU_IMAGE_ANNOTATION_ENGLISH_2000,

    /**
     * 天地图地形国家2000坐标系地图服务
     */
    TIANDITU_TERRAIN_2000,

    /**
     * 天地图地形国家2000坐标系中文标注
     */
    TIANDITU_TERRAIN_ANNOTATION_CHINESE_2000, HD_ANNOTATION_CHINESE_2000
}