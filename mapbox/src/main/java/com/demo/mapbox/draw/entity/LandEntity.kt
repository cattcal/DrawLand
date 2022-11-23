package com.demo.mapbox.draw.entity

import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc: 每次操作 entity
 */
class LandEntity(val selectPosition: Int, latLngs: List<LatLng?>?) {
    var latLngList: MutableList<LatLng?> = ArrayList()

    init {
        latLngList.addAll(latLngs!!)
    }


}