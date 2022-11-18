package com.demo.location.location

/**
 * @author guoyalong
 * @time 2022/11/08
 * @desc 定位接口
 */
interface ILocation {
    /**
     * 维度
     *
     * @return 维度
     */
    val latitude: Double

    /**
     * 经度
     *
     * @return 经度
     */
    val longitude: Double

    /**
     * 时间
     *
     * @return 定位时间毫秒值
     */
    val time: Long

    /**
     * 获取当前位置
     *
     * @return 当前位置
     */
    val address: String?

    /**
     * 获取当前定位结果来源，如网络定位结果，详见定位类型表
     *
     * @return 定位来源信息
     */
    val locationType: Int

    /**
     * 获取精度信息
     *
     * @return 精度信息
     */
    val accuracy: Float

    /**
     * 获取国家信息
     *
     * @return 国家信息
     */
    val country: String?

    /**
     * 获取省信息
     *
     * @return 省信息
     */
    val province: String?

    /**
     * 获取城市信息
     *
     * @return 城市信息
     */
    val city: String?

    /**
     * 获取城区信息
     *
     * @return 城区信息
     */
    val district: String?

    /**
     * 获取街道信息
     *
     * @return 街道信息
     */
    val street: String?

    /**
     * 获取街道门牌号信息
     *
     * @return 街道门牌号信息
     */
    val streetNum: String?

    /**
     * 获取城市编码
     *
     * @return 城市编码
     */
    val cityCode: String?

    /**
     * 获取地区编码
     *
     * @return 地区编码
     */
    val adCode: String?

    /**
     * 获取获取当前定位点的AOI信息
     *
     * @return 获取当前定位点的AOI信息
     */
    val aoiName: String?

    /**
     * 获取获取当前室内定位的建筑物Id
     *
     * @return 获取当前室内定位的建筑物Id
     */
    val buildingId: String?

    /**
     * 获取获取当前室内定位的楼层
     *
     * @return 获取当前室内定位的楼层
     */
    val floor: String?

    /**
     * 获取获取GPS的当前状态
     *
     * @return 获取GPS的当前状态
     */
    val gpsAccuracyStatus: Int
}