package com.demo.drawland

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.asLiveData
import com.demo.drawland.aop.Permissions
import com.hjq.shape.view.ShapeButton
import com.demo.drawland.aop.SingleClick
import com.demo.drawland.app.AppActivity
import com.demo.drawland.http.PageList
import com.demo.drawland.http.bean.Article
import com.hjq.gson.factory.GsonFactory
import com.hjq.permissions.Permission
import com.xdzt.mapbox.util.AssetsUtil
import com.xdzt.mapbox.util.ReadKml
import me.rosuh.filepicker.config.FilePickerManager
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toFlowResponse


class MainActivity : AppActivity() {

    private val bidInsView: ShapeButton? by lazy { findViewById(R.id.btn_bid_ins) }
    private val getView: ShapeButton? by lazy { findViewById(R.id.btn_get) }
    private val parseKmlView: ShapeButton? by lazy { findViewById(R.id.btn_parse_kml) }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        setOnClickListener(bidInsView, getView, parseKmlView)
    }

    override fun initData() {

    }

    @SingleClick
    override fun onClick(view: View) {
        when (view) {
            bidInsView -> {
                extracted()
            }

            getView -> {
                getArticle()
            }

            parseKmlView -> {
                startSelectFile()
            }
        }
    }

    private fun getArticle() {
        RxHttp.get("/article/list/0/json").toFlowResponse<PageList<Article>>().asLiveData()
            .observe(this) {
                toast(GsonFactory.getSingletonGson().toJson(it))
            }
    }

    @Permissions(
        Permission.ACCESS_FINE_LOCATION,
        Permission.ACCESS_COARSE_LOCATION,
        Permission.ACCESS_BACKGROUND_LOCATION
    )
    private fun extracted() {
        startActivity(DrawLandActivity::class.java)
    }

    @Permissions(
        Permission.MANAGE_EXTERNAL_STORAGE
    )
    private fun startSelectFile(){
        FilePickerManager.from(this).forResult(FilePickerManager.REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FilePickerManager.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val list = FilePickerManager.obtainData()
                    // do your work

                } else {
                    toast("没有选择任何东西~")
                }
            }
        }
    }
}