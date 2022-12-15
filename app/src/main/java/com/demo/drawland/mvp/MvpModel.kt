package com.demo.drawland.mvp

/**
 * @author: hujw
 * @time: 2022/12/5
 * @desc:  MVP 模型基类
 */
abstract class MvpModel<L> {
    private var mListener: L? = null
    fun setListener(listener: L) {
        mListener = listener
    }

    fun getListener(): L? {
        return mListener
    }
}