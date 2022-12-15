package com.demo.drawland.mvp

import android.content.Context

/**
 * @author: hujw
 * @time: 2022/12/5
 * @desc:  MVP 通用性接口
 */
interface IMvpView {
    /**
     * 获取上下文对象
     */
    fun getContext(): Context?

    /**
     * 加载中
     */
    fun onLoading()

    /**
     * 加载完成
     */
    fun onComplete()

    /**
     * 用于请求的数据为空的状态
     */
    fun onEmpty()

    /**
     * 用于请求数据出错
     */
    fun onError()
}