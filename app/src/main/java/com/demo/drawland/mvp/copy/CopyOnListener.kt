package com.demo.drawland.mvp.copy

/**
 * @author: hujw
 * @time: 2022/12/5
 * @desc: 可进行拷贝的监听器
 */
interface CopyOnListener {
    fun onSucceed(data: List<String?>?)
    fun onFail(msg: String?)
}