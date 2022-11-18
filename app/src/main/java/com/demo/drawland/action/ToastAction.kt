package com.demo.drawland.action

import androidx.annotation.StringRes
import com.hjq.toast.ToastUtils

/**
 * author :hujw
 * time : 2022/11/11
 * desc : 吐司意图
 */
interface ToastAction {

    fun toast(text: CharSequence?) {
        ToastUtils.show(text)
    }

    fun toast(@StringRes id: Int) {
        ToastUtils.show(id)
    }

    fun toast(`object`: Any?) {
        ToastUtils.show(`object`)
    }
}