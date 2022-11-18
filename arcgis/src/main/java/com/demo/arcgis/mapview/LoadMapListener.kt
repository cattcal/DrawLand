package com.demo.arcgis.mapview

/**
 * @author guoyalong
 * @time 2022/11/03
 * @desc 地图加载监听
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