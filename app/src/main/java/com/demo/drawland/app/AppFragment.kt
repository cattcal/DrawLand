package com.demo.drawland.app

import com.demo.base.BaseFragment
import com.demo.drawland.action.ToastAction

/**
 * author :hujw
 * time : 2022/11/14
 * desc :  Fragment 业务基类
 */
abstract class AppFragment<A : AppActivity> : BaseFragment<A>(),
    ToastAction {

    /**
     * 当前加载对话框是否在显示中
     */
    open fun isShowDialog(): Boolean {
        val activity: A = getAttachActivity() ?: return false
        return activity.isShowDialog()
    }

    /**
     * 显示加载对话框
     */
    open fun showDialog() {
        getAttachActivity()?.showDialog()
    }

    /**
     * 隐藏加载对话框
     */
    open fun hideDialog() {
        getAttachActivity()?.hideDialog()
    }
}