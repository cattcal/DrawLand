package com.demo.location.poi

import com.amap.api.services.poisearch.PoiResultV2
import com.amap.api.services.poisearch.PoiSearchV2
import com.amap.api.services.core.PoiItemV2
import com.amap.api.services.core.AMapException
import android.content.Context

/**
 * @author guoyalong
 * @time 2022/11/08
 * @desc poi 搜索操作
 */
class PoiSearchOperations(private val mContext: Context?) {
    private var mPoiSearchListener: IPoiSearchListener? = null

    /**
     * 开始搜索
     *
     * @param address  地址
     * @param pageSize 每页显示数量
     * @param pageNum  页数
     */
    fun startSearch(address: String?, pageSize: Int, pageNum: Int) {
        val query = PoiSearchV2.Query(address, "", "")
        query.pageSize = pageSize
        query.pageNum = pageNum
        val poiSearch: PoiSearchV2
        try {
            if (mPoiSearchListener != null) {
                mPoiSearchListener!!.startSearch()
            }
            poiSearch = PoiSearchV2(mContext, query)
            poiSearch.setOnPoiSearchListener(object : PoiSearchV2.OnPoiSearchListener {
                override fun onPoiSearched(poiResultV2: PoiResultV2, i: Int) {
                    if (mPoiSearchListener != null) {
                        mPoiSearchListener!!.searchSuccess(poiResultV2)
                    }
                }

                override fun onPoiItemSearched(poiItemV2: PoiItemV2, i: Int) {}
            }) //设置监听
            poiSearch.searchPOIAsyn() //调用搜索
        } catch (e: AMapException) {
            e.printStackTrace()
            if (mPoiSearchListener != null) {
                mPoiSearchListener!!.searchFailure()
            }
        }
    }

    fun setPoiSearchListener(listener: IPoiSearchListener?) {
        mPoiSearchListener = listener
    }
}