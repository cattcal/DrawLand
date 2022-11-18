package com.demo.mapbox.draw.entity

import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
 */
class LandEntity(val selectPosition: Int, latLngs: List<LatLng>?) {
    private val latLngList: MutableList<LatLng>

    init {
        latLngList = ArrayList()
        latLngList.addAll(latLngs!!)
    }

    fun getLatLngList(): List<LatLng> {
        return latLngList
    }
}