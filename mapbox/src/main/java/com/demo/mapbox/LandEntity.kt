package com.demo.drawland

import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * @author: hujw
 * @time: 2022/11/21
 * @desc:
 */
class LandEntity  {
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
            : MutableList<PathBean>? = null

    @Transient
     var center // 中心点
            : LatLng? = null

    @Transient
     var latLngs // 所有点
            : MutableList<LatLng?>? = null



    class PathBean {
        /**
         * longitude : 119.46041618068
         * latitude : 25.9918393779542
         */
        var longitude // 经度
                = 0.0
        var latitude // 纬度
                = 0.0

        override fun toString(): String {
            return "PathBean{" +
                    "longitude=" + longitude +
                    ", latitude=" + latitude +
                    '}'
        }
    }

    override fun toString(): String {
        return "com.demo.drawland.LandEntity{" +
                "id='" + id + '\'' +
                ", area=" + area +
                ", perimeter=" + perimeter +
                ", userName='" + userName + '\'' +
                ", state=" + state +
                ", path=" + path +
                '}'
    }
}
