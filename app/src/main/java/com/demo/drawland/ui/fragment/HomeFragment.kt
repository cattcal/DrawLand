package com.demo.drawland.ui.fragment

import android.util.Log
import android.view.View
import com.amap.api.services.poisearch.PoiResultV2
import com.demo.drawland.R
import com.demo.drawland.app.TitleBarFragment
import com.demo.drawland.ui.activity.HomeActivity
import com.xdzt.location.poi.IPoiSearchListener
import com.xdzt.location.poi.PoiSearchOperations
import com.xdzt.mapbox.draw.BaseDrawMapView
import com.xdzt.mapbox.draw.manager.DrawType

/**
 * author :hujw
 * time : 2022/11/14
 * desc : 首页 Fragment
 */
class HomeFragment : TitleBarFragment<HomeActivity>() {

    companion object {

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    var mBaseDrawMapView: BaseDrawMapView? = null

    override fun getLayoutId(): Int {
        return R.layout.arcgis_fragment
    }

    override fun initView() {
        mBaseDrawMapView = findViewById(R.id.base_draw_map_view)
        mBaseDrawMapView?.initDrawMapView()
        findViewById<View>(R.id.btn_switch)?.setOnClickListener { view: View? ->
            mBaseDrawMapView!!.changeImageLayer("天地图")
        }
        findViewById<View>(R.id.btn_location)?.setOnClickListener { v: View? ->
            mBaseDrawMapView!!.baseMapView!!.startLocation()
        }
        findViewById<View>(R.id.btn_location1)?.setOnClickListener { v: View? ->
            val operations = PoiSearchOperations(context)
            operations.setPoiSearchListener(object : IPoiSearchListener {
                override fun startSearch() {}
                override fun searchFailure() {}
                override fun searchSuccess(poiResultV2: PoiResultV2) {
                    val pois = poiResultV2.pois
                    for (poiItemV2 in pois) {
                        Log.i(
                            "gyl",
                            poiItemV2.title + "," + poiItemV2.latLonPoint.longitude + "," + poiItemV2.latLonPoint.latitude
                        )
                    }
                }
            })
            operations.startSearch("河南省禹州市鸠山镇官庄窖村", 10, 1)
        }
        findViewById<View>(R.id.btn_draw_land)?.setOnClickListener {
            mBaseDrawMapView!!.startDraw(
                DrawType.DRAW_LAND
            )
        }
        findViewById<View>(R.id.btn_add_point)?.setOnClickListener {
            mBaseDrawMapView!!.addPoint(
                mBaseDrawMapView!!.baseMapView!!.center
            )
        }
        findViewById<View>(R.id.btn_delete)?.setOnClickListener { mBaseDrawMapView!!.delete() }
        findViewById<View>(R.id.btn_undo)?.setOnClickListener { mBaseDrawMapView!!.undo() }
        findViewById<View>(R.id.btn_complete)?.setOnClickListener { mBaseDrawMapView!!.complete() }
        findViewById<View>(R.id.btn_draw_length)?.setOnClickListener {
            mBaseDrawMapView!!.startDraw(
                DrawType.MEASURE_LENGTH
            )
        }
        findViewById<View>(R.id.btn_draw_area)?.setOnClickListener {
            mBaseDrawMapView!!.startDraw(
                DrawType.MEASURE_AREA
            )
        }

    }

    override fun initData() {

    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }
    override fun onPause() {
        mBaseDrawMapView?.onPause()
        super.onPause()
    }

    override fun onResume() {
        mBaseDrawMapView?.onResume()
        super.onResume()
    }


    override fun onDestroy() {
        mBaseDrawMapView?.onDestroy()
        super.onDestroy()

    }

}