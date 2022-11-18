package com.demo.location.location

import com.demo.location.location.ILocation

/**
 * @author guoyalong
 * @time 2022/11/08
 * @desc 定位监听
 */
interface ILocationListener {
    /**
     * 定位成功
     *
     * @param location 定位信息
     */
    fun onLocationSuccess(location: ILocation)

    /**
     * 定位失败
     *
     * @param message 信息
     */
    fun onLocationFailure(message: String?)
}