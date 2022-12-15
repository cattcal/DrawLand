package com.demo.drawland.mvp.copy

import com.demo.drawland.mvp.MvpInject
import com.demo.drawland.mvp.MvpPresenter

/**
 * @author: hujw
 * @time: 2022/12/5
 * @desc: 可进行拷贝的业务处理类
 */
class CopyPresenter : MvpPresenter<CopyContract.View?>(), CopyContract.Presenter,
    CopyOnListener {

    @MvpInject
    var mModel: CopyModel? = null

    /**
     * [CopyContract.Presenter]
     */
    override fun login(account: String?, password: String?) {
        mModel?.setAccount(account)
        mModel?.setPassword(password)
        mModel?.setListener(this)
        mModel?.login()
    }

    /**
     * [CopyOnListener]
     */
    override fun onSucceed(data: List<String?>?) {
        view?.loginSuccess(data)
    }

    override fun onFail(msg: String?) {
        view?.loginError(msg)
    }
}