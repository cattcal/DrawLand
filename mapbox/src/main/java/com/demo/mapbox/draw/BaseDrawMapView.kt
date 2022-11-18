package com.demo.mapbox.draw

import android.content.Context
import android.graphics.PointF
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.mapbox.mapboxsdk.geometry.LatLng
import com.demo.mapbox.R
import com.demo.mapbox.draw.manager.DrawLandManager
import com.demo.mapbox.draw.manager.DrawType
import com.demo.mapbox.draw.manager.IMoveListener
import com.demo.mapbox.draw.view.LandView
import com.demo.mapbox.draw.view.MirrorMapView
import com.demo.mapbox.mapview.BaseMapView
import com.demo.mapbox.mapview.LoadMapListener

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc: 基础地块勾画类
 */
class BaseDrawMapView @JvmOverloads constructor(
    private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1
) : FrameLayout(
    mContext, attrs, defStyleAttr
), IBaseDrawMapView {
    override var baseMapView: BaseMapView? = null
    override var drawLandManager: DrawLandManager? = null
    private var mLandView: LandView? = null
    private var mMirrorMapViewFront: MirrorMapView? = null
    private var mMirrorMapViewBack: MirrorMapView? = null

    init {
        val view = LayoutInflater.from(mContext).inflate(R.layout.base_mapbox_draw_land_view, this)
        initView(view)
    }

    private fun initView(view: View) {
        baseMapView = view.findViewById(R.id.baseMapView)
        mLandView = view.findViewById(R.id.landView)
        mMirrorMapViewFront = view.findViewById(R.id.mirror_map_view_front)
        mMirrorMapViewBack = view.findViewById(R.id.mirror_map_view_back)
        post { hideMirror() }
    }

    /**
     * 初始化地块勾画 view
     */
    fun initDrawMapView() {
        baseMapView?.setLoadMapListener(object : LoadMapListener {
            override fun onLoading() {}
            override fun onLoadComplete() {
                drawLandManager!!.addTouchListener()
                drawLandManager!!.addMapClickListener()
                drawLandManager!!.addMapLongClickListener()
            }

            override fun onFailed(code: Int, message: String?) {}
        })
        baseMapView!!.initMapView()
        drawLandManager = DrawLandManager(mContext, baseMapView!!)
        drawLandManager!!.setOnMoveListener(object : IMoveListener {
            override fun startMove(
                event: MotionEvent?, points: MutableList<PointF>?, selectIndex: Int
            ) {
                mLandView!!.setPoints(points, selectIndex)
                showMirror()
                mMirrorMapViewFront!!.mirrorLandView?.setPoints(points, selectIndex)
                mMirrorMapViewBack!!.mirrorLandView?.setPoints(points, selectIndex)
            }

            override fun onTouchEvent(event: MotionEvent?) {
                mLandView!!.setTouchEvent(event)
                mMirrorMapViewFront!!.mirrorLandView?.setTouchEvent(event)
                mMirrorMapViewBack!!.mirrorLandView?.setTouchEvent(event)
                if (event!!.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    mMirrorMapViewFront!!.mirrorLandView?.setPoints(mLandView!!.points)
                    mMirrorMapViewBack!!.mirrorLandView?.setPoints(mLandView!!.points)
                    val latLng =
                        baseMapView!!.map!!.projection.fromScreenLocation(PointF(event.x, event.y))
                    mMirrorMapViewFront!!.baseMapView?.setCenter(
                        latLng, baseMapView!!.map!!.cameraPosition
                    )
                    mMirrorMapViewBack!!.baseMapView?.setCenter(
                        latLng, baseMapView!!.map!!.cameraPosition
                    )
                } else if (event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP) {
                    hideMirror()
                }
            }

            override fun onMapLongClicked(isSelect: Boolean) {}
            override fun onScaleEnd() {}
            override fun onSingleTapUp(isSelect: Boolean, event: MotionEvent?) {}
        })
    }

    override fun changeImageLayer(name: String) {
        if (baseMapView != null) {
            baseMapView!!.changeImageLayer(name)
            if (baseMapView!!.isShowVector != mMirrorMapViewFront!!.baseMapView?.isShowVector) {
                mMirrorMapViewFront!!.baseMapView?.changeImageLayer(name)
            }
            if (baseMapView!!.isShowVector != mMirrorMapViewBack!!.baseMapView?.isShowVector) {
                mMirrorMapViewBack!!.baseMapView?.changeImageLayer(name)
            }
        }
    }

    override fun addPoint(point: LatLng?) {
        drawLandManager?.add(point)
    }

    override fun delete() {
        drawLandManager?.delete()
    }

    override fun undo() {
        drawLandManager?.undo()
    }

    override fun complete() {
        drawLandManager?.complete()
    }

    override fun startDraw(drawType: DrawType?) {
        if (drawLandManager != null) {
            drawLandManager!!.startDraw(drawType)
        }
        if (mLandView != null) {
            mLandView!!.setDrawType(drawType)
            mMirrorMapViewFront!!.mirrorLandView?.setDrawType(drawType)
            mMirrorMapViewBack!!.mirrorLandView?.setDrawType(drawType)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (baseMapView != null) {
            baseMapView!!.onCreate(savedInstanceState)
        }
        mMirrorMapViewFront!!.baseMapView?.onCreate(savedInstanceState)
        mMirrorMapViewBack!!.baseMapView?.onCreate(savedInstanceState)
    }

    override fun onStart() {
        if (baseMapView != null) {
            baseMapView!!.onStart()
        }
        mMirrorMapViewFront!!.baseMapView?.onStart()
        mMirrorMapViewBack!!.baseMapView?.onStart()
    }

    override fun onResume() {
        if (baseMapView != null) {
            baseMapView!!.onResume()
        }
        mMirrorMapViewFront!!.baseMapView?.onResume()
        mMirrorMapViewBack!!.baseMapView?.onResume()
    }

    override fun onPause() {
        if (baseMapView != null) {
            baseMapView!!.onPause()
        }
        mMirrorMapViewFront!!.baseMapView?.onPause()
        mMirrorMapViewBack!!.baseMapView?.onPause()
    }

    override fun onStop() {
        if (baseMapView != null) {
            baseMapView!!.onStop()
        }
        mMirrorMapViewFront!!.baseMapView?.onStop()
        mMirrorMapViewBack!!.baseMapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if (baseMapView != null) {
            baseMapView!!.onSaveInstanceState(outState!!)
        }
        mMirrorMapViewFront!!.baseMapView?.onSaveInstanceState(outState!!)
        mMirrorMapViewBack!!.baseMapView?.onSaveInstanceState(outState!!)
    }

    override fun onLowMemory() {
        if (baseMapView != null) {
            baseMapView!!.onLowMemory()
        }
        mMirrorMapViewFront!!.baseMapView?.onLowMemory()
        mMirrorMapViewBack!!.baseMapView?.onLowMemory()
    }

    override fun onDestroy() {
        if (baseMapView != null) {
            baseMapView!!.onDestroy()
        }
        mMirrorMapViewFront!!.baseMapView?.onDestroy()
        mMirrorMapViewBack!!.baseMapView?.onDestroy()
    }

    /**
     * 隐藏镜面效果
     */
    private fun hideMirror() {
        val layoutParamsFront = mMirrorMapViewFront!!.layoutParams as RelativeLayout.LayoutParams
        layoutParamsFront.topMargin = -mMirrorMapViewFront!!.height
        mMirrorMapViewFront!!.layoutParams = layoutParamsFront
        val layoutParamsBack = mMirrorMapViewBack!!.layoutParams as RelativeLayout.LayoutParams
        layoutParamsBack.topMargin = -mMirrorMapViewBack!!.height
        mMirrorMapViewBack!!.layoutParams = layoutParamsBack
    }

    /**
     * 显示镜面效果
     */
    private fun showMirror() {
        val layoutParamsFront = mMirrorMapViewFront!!.layoutParams as RelativeLayout.LayoutParams
        layoutParamsFront.topMargin = 0
        mMirrorMapViewFront!!.layoutParams = layoutParamsFront
        val layoutParamsBack = mMirrorMapViewBack!!.layoutParams as RelativeLayout.LayoutParams
        layoutParamsBack.topMargin = 0
        mMirrorMapViewBack!!.layoutParams = layoutParamsBack
    }
}