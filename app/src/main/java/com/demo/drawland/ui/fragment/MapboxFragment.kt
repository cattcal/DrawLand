package com.demo.drawland.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.amap.api.services.poisearch.PoiResultV2
import com.google.gson.reflect.TypeToken
import com.hjq.gson.factory.GsonFactory
import com.mapbox.geojson.Feature
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.demo.base.BaseDialog
import com.demo.drawland.R
import com.demo.drawland.aop.SingleClick
import com.demo.drawland.app.TitleBarFragment
import com.demo.drawland.model.LandInfoBean
import com.demo.drawland.ui.activity.HomeActivity
import com.demo.drawland.ui.dialog.SelectDialog
import com.demo.location.poi.IPoiSearchListener
import com.demo.location.poi.PoiSearchOperations
import com.demo.mapbox.draw.BaseDrawMapView
import com.demo.mapbox.draw.manager.DrawType
import com.demo.mapbox.util.AssetsUtil

/**
 * author :hujw
 * time : 2022/11/14
 * desc : 首页 Fragment
 */
class MapboxFragment : TitleBarFragment<HomeActivity>() {

    companion object {
        var layerPos = 0

        fun newInstance(): MapboxFragment {
            return MapboxFragment()
        }
    }

    var mBaseDrawMapView: BaseDrawMapView? = null
    private val mAlreadyLands = mutableListOf<LandInfoBean>()

    override fun getLayoutId(): Int {
        return R.layout.mapbox_fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBaseDrawMapView?.onCreate(savedInstanceState)
    }

    override fun initView() {
        mBaseDrawMapView = findViewById(R.id.base_draw_map_view)
        mBaseDrawMapView?.initDrawMapView()

        setOnClickListener(R.id.iv_player_manager, R.id.iv_location)

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
            operations.startSearch("山西省万荣县薛吉村", 10, 1)
        }
        findViewById<View>(R.id.btn_draw_land)?.setOnClickListener {
            mBaseDrawMapView?.startDraw(
                DrawType.DRAW_LAND
            )
        }
        findViewById<View>(R.id.btn_add_point)?.setOnClickListener {
            mBaseDrawMapView?.addPoint(
                mBaseDrawMapView?.baseMapView!!.center
            )
        }
        findViewById<View>(R.id.btn_delete)?.setOnClickListener { mBaseDrawMapView?.delete() }
        findViewById<View>(R.id.btn_undo)?.setOnClickListener { mBaseDrawMapView?.undo() }
        findViewById<View>(R.id.btn_complete)?.setOnClickListener {
            mBaseDrawMapView?.complete()
        }
        findViewById<View>(R.id.btn_draw_length)?.setOnClickListener {
            mBaseDrawMapView?.startDraw(
                DrawType.MEASURE_LENGTH
            )
        }
        findViewById<View>(R.id.btn_draw_area)?.setOnClickListener {
            mBaseDrawMapView?.startDraw(
                DrawType.MEASURE_AREA
            )
        }
    }

    override fun initData() {

        val landJson = AssetsUtil.loadStringFromAssets(context, "land.json")

        if (!TextUtils.isEmpty(landJson)) {
            val landInfos: MutableList<LandInfoBean> = GsonFactory.getSingletonGson()
                .fromJson(landJson, object : TypeToken<MutableList<LandInfoBean>>() {}.type)
            if (landInfos.isNotEmpty()) {
                mAlreadyLands.addAll(landInfos)
            }
        }

        mAlreadyLands.forEach {
            it.state = 0
            val latLngBounds = LatLngBounds.Builder().includes(it.path!!).build()
            it.center = latLngBounds.center
            it.latLngs = it.path!!
        }

        showLand()
    }

    private fun showLand() {
        val features = mutableListOf<Feature>()
        val latLngList = mutableListOf<LatLng>()
        mAlreadyLands.forEach {

        }


    }

    @SingleClick
    override fun onClick(view: View) {
        when (view.id) {
            R.id.iv_player_manager -> {
                SelectDialog.Builder(requireContext()).setTitle("请选择底图")
                    .setList("Mapbox", "天地图", "星图地球")
                    // 设置单选模式
                    .setSingleSelect()
                    // 设置默认选中
                    .setSelect(layerPos).setListener(object : SelectDialog.OnListener<String> {
                        override fun onSelected(dialog: BaseDialog?, data: HashMap<Int, String>) {
                            data.keys.forEach {
                                when (it) {
                                    0 -> {
                                        mBaseDrawMapView?.changeImageLayer("Mapbox")
                                    }

                                    1 -> {
                                        mBaseDrawMapView?.changeImageLayer("天地图")
                                    }

                                    2 -> {
                                        mBaseDrawMapView?.changeImageLayer("星图地球")
                                    }
                                }
                                layerPos = it
                            }
                        }

                        override fun onCancel(dialog: BaseDialog?) {

                        }
                    }).show()
            }

            R.id.iv_location -> {
                mBaseDrawMapView?.baseMapView?.startLocation()
            }
        }
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