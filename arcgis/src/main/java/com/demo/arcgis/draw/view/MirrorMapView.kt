package com.demo.arcgis.draw.view

import kotlin.jvm.JvmOverloads
import android.widget.FrameLayout
import com.demo.arcgis.mapview.BaseMapView
import android.view.LayoutInflater
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.demo.arcgis.R

/**
 * @author guoyalong
 * @time 2022/11/09
 * @desc 底图镜像 MapView
 */
class MirrorMapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1
) : FrameLayout(
    context, attrs, defStyleAttr
) {
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
        val view = LayoutInflater.from(context).inflate(R.layout.base_mirror_map_view, this)
        initView(view)
    }

    private fun initView(view: View) {
        baseMapView = view.findViewById(R.id.base_map_view)
        mirrorLandView = view.findViewById(R.id.mirror_land_view)
        baseMapView?.initMap("", "")
    }
}