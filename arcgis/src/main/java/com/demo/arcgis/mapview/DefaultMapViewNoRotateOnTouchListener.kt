package com.demo.arcgis.mapview

import com.esri.arcgisruntime.mapping.view.MapView
import android.view.MotionEvent
import android.content.Context
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener

/**
 * @author guoyalong
 * @time 2022/11/03
 * @desc 禁止 arcgis 旋转操作
 */
open class DefaultMapViewNoRotateOnTouchListener(context: Context?, mapView: MapView?) :
    DefaultMapViewOnTouchListener(context, mapView) {
    override fun onRotate(event: MotionEvent, rotationAngle: Double): Boolean {
        return false
    }
}