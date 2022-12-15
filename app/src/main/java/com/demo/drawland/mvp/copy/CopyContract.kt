package com.demo.drawland.mvp.copy

import com.demo.drawland.mvp.IMvpView

/**
 * @author: hujw
 * @time: 2022/12/5
 * @desc: 可进行拷贝的契约类
 */
class CopyContract {
    interface View : IMvpView {
        fun loginSuccess(data: List<String?>?)
        fun loginError(msg: String?)
    }

    interface Presenter {
        fun login(account: String?, password: String?)
    }
}