package com.demo.mapbox.mapview

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc: 地图加载监听
 */
interface LoadMapListener {
    /**
     * 正在加载中
     */
    fun onLoading()

    /**
     * 加载成功
     */
    fun onLoadComplete()

    /**
     * 加载失败
     *
     * @param code    失败code
     * @param message 失败message
     */
    fun onFailed(code: Int, message: String?)
}