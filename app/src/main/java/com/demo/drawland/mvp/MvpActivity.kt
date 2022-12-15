package com.demo.drawland.mvp

import com.demo.drawland.app.AppActivity
import com.demo.drawland.mvp.proxy.IMvpPresenterProxy
import com.demo.drawland.mvp.proxy.MvpPresenterProxyImpl

/**
 * @author: hujw
 * @time: 2022/12/5
 * @desc:
 */
abstract class MvpActivity: AppActivity(),IMvpView {

    private var mMvpProxy: IMvpPresenterProxy? = null

    override fun initActivity() {
        mMvpProxy = createPresenterProxy()
        mMvpProxy?.bindPresenter()
        super.initActivity()

    }

    protected open fun createPresenterProxy(): IMvpPresenterProxy? {
        return MvpPresenterProxyImpl(this)
    }

    override fun onDestroy() {
        mMvpProxy?.unbindPresenter()
        super.onDestroy()
    }

    override fun onLoading() {
        showDialog()
    }

    override fun onComplete() {
        hideDialog()
    }

    override fun onEmpty() {

    }

    override fun onError() {

    }

}