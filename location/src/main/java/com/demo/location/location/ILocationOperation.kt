package com.demo.location.location

import com.demo.location.location.ILocationListener

/**
 * @author guoyalong
 * @time 2022/11/08
 * @desc 定位一些操作
 */
interface ILocationOperation {
    /**
     * 开始定位
     */
    fun start()

    /**
     * 停止定位
     */
    fun stop()

    /**
     * 销毁定位
     */
    fun destroy()

    /**
     * 是否开始
     *
     * @return bool
     */
    val isStart: Boolean

    /**
     * 设置定位监听
     *
     * @param locationListener 定位监听
     */
    fun setLocationListener(locationListener: ILocationListener?)
}