package com.demo.drawland.mvp.copy

import android.view.View
import com.demo.drawland.R
import com.demo.drawland.mvp.MvpActivity
import com.demo.drawland.mvp.MvpInject

/**
 * @author: hujw
 * @time: 2022/12/5
 * @desc: 可进行拷贝的MVP Activity 类
 */
class CopyMvpActivity : MvpActivity(), CopyContract.View {

    @MvpInject
    var mPresenter: CopyPresenter? = null

    override fun getLayoutId(): Int {
        return R.layout.activiy_copy
    }

    override fun initView() {

    }

    override fun initData() {

    }

    fun onLogin(view: View?) {
        // 登录操作
        mPresenter?.login("账户", "密码")
    }


    override fun loginSuccess(data: List<String?>?) {
        toast("登录成功了")
    }

    override fun loginError(msg: String?) {
        toast(msg)
    }
}