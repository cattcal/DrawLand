package com.demo.drawland.mvp.copy

import com.demo.drawland.mvp.MvpModel

/**
 * @author: hujw
 * @time: 2022/12/5
 * @desc:
 */
class CopyModel : MvpModel<CopyOnListener?>() {
    private var mAccount: String? = null
    private var mPassword: String? = null
    fun setAccount(account: String?) {
        mAccount = account
    }

    fun setPassword(password: String?) {
        mPassword = password
    }

    fun login() {
        // 为了省事，这里直接回调成功
        if ("账户" == mAccount && "密码" == mPassword) {
            getListener()?.onSucceed(null)
        } else {
            getListener()?.onFail("账户或密码不对哦")
        }
    }
}