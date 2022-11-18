package com.demo.drawland.model

import com.mapbox.mapboxsdk.geometry.LatLng
import com.demo.mapbox.draw.entity.LandEntity

/**
 * @author: hujw
 * @time: 2022/11/17
 * @desc:
 */
class LandInfoBean {

    var id // 地块id
            : String? = null
    var area // 面积 亩
            = 0.0
    var perimeter // 周长 米
            = 0.0
    var userName // 用户名
            : String? = null
    var state = 0 // 状态  0表示没有更改，1代表修改，2代表删除  3表示新增

    var path // 地块边界
            : List<LatLng>? = null

    @Transient
    var center // 中心点
            : LatLng? = null

    @Transient
    var latLngs // 所有点
            : List<LatLng>? = null
}