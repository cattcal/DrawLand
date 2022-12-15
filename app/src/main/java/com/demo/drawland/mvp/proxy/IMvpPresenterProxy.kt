package com.demo.drawland.mvp.proxy

/**
 * @author: hujw
 * @time: 2022/12/5
 * @desc: 逻辑层代理接口
 */
interface IMvpPresenterProxy {
    /**
     * 绑定 Presenter
     */
    fun bindPresenter()

    /**
     * 解绑 Presenter
     */
    fun unbindPresenter()
}