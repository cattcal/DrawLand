package com.demo.location.poi

import com.amap.api.services.poisearch.PoiResultV2

/**
 * @author guoyalong
 * @time 2022/11/08
 * @desc poi 搜索操作
 */
interface IPoiSearchListener {
    /**
     * 开始搜索
     */
    fun startSearch()

    /**
     * 搜索失败
     */
    fun searchFailure()

    /**
     * 搜索成功
     *
     * @param poiResultV2 搜索结果
     */
    fun searchSuccess(poiResultV2: PoiResultV2)
}