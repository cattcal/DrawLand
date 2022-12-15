package com.demo.drawland.mvp.proxy

/**
 * @author: hujw
 * @time: 2022/12/5
 * @desc: 模型层代理接口
 */
interface IMvpModelProxy {
    /**
     * 绑定 Model
     */
    fun bindModel()

    /**
     * 解绑 Model
     */
    fun unbindModel()
}