package com.demo.mapbox.draw.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.demo.mapbox.R
import com.demo.mapbox.mapview.BaseMapView

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
 */
class MirrorMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : FrameLayout(context, attrs, defStyleAttr) {
    /**
     * 获取 baseMapView
     *
     * @return baseMapView
     */
    var baseMapView: BaseMapView? = null
        private set

    /**
     * 获取 镜像landView
     *
     * @return 镜像landView
     */
    var mirrorLandView: MirrorLandView? = null
        private set

    init {
        val view = inflate(context, R.layout.base_mapbox_mirror_map_view, this)
        initView(view)
    }

    private fun initView(view: View) {
        baseMapView = view.findViewById(R.id.base_map_view)
        mirrorLandView = view.findViewById(R.id.mirror_land_view)
        baseMapView?.initMapView()
    }
}