package com.demo.location.location

import com.amap.api.location.AMapLocation
import com.demo.location.location.ILocation

/**
 * @author guoyalong
 * @time 2022/11/08
 * @desc 基础定位
 */
class BaseLocation internal constructor(private val mMapLocation: AMapLocation) : ILocation {
    override val latitude: Double
        get() = mMapLocation.latitude
    override val longitude: Double
        get() = mMapLocation.longitude
    override val time: Long
        get() = mMapLocation.time
    override val address: String?
        get() = mMapLocation.address
    override val locationType: Int
        get() = mMapLocation.locationType
    override val accuracy: Float
        get() = mMapLocation.accuracy
    override val country: String?
        get() = mMapLocation.country
    override val province: String?
        get() = mMapLocation.province
    override val city: String?
        get() = mMapLocation.city
    override val district: String?
        get() = mMapLocation.district
    override val street: String?
        get() = mMapLocation.street
    override val streetNum: String?
        get() = mMapLocation.streetNum
    override val cityCode: String?
        get() = mMapLocation.cityCode
    override val adCode: String?
        get() = mMapLocation.adCode
    override val aoiName: String?
        get() = mMapLocation.aoiName
    override val buildingId: String?
        get() = mMapLocation.buildingId
    override val floor: String?
        get() = mMapLocation.floor
    override val gpsAccuracyStatus: Int
        get() = mMapLocation.gpsAccuracyStatus
}