package com.demo.arcgis.draw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.graphics.PointF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.esri.arcgisruntime.mapping.Viewpoint
import com.demo.arcgis.R
import com.demo.arcgis.draw.manager.DrawLandManager
import com.demo.arcgis.draw.manager.DrawType
import com.demo.arcgis.draw.manager.IMoveListener
import com.demo.arcgis.draw.view.LandView
import com.demo.arcgis.draw.view.MirrorMapView
import com.demo.arcgis.mapview.BaseMapView
import com.demo.arcgis.mapview.LoadMapListener

/**
 * @author guoyalong
 * @time 2022/11/09
 * @desc 基础地块勾画类
 */
class BaseDrawMapView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : FrameLayout(
    mContext, attrs, defStyleAttr
), IBaseDrawMapView {
    override var baseMapView: BaseMapView? = null
        private set
    override var drawLandManager: DrawLandManager? = null
        private set
    private var mLandView: LandView? = null
    private var mMirrorMapViewFront: MirrorMapView? = null
    private var mMirrorMapViewBack: MirrorMapView? = null

    init {
        val view = LayoutInflater.from(mContext).inflate(R.layout.base_draw_land_view, this)
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
        baseMapView!!.setOnLoadMapListener(object : LoadMapListener {
            override fun onLoading() {}
            @SuppressLint("ClickableViewAccessibility")
            override fun onLoadComplete() {
            }

            override fun onFailed(code: Int, message: String?) {}
        })
        baseMapView!!.initMap("", "")
        drawLandManager = DrawLandManager(mContext, baseMapView?.mapView)
        drawLandManager!!.setOnMoveListener(object : IMoveListener {
            override fun startMove(event: MotionEvent?, points: MutableList<PointF?>, selectIndex: Int) {
                mLandView?.setPoints(points, selectIndex)
                showMirror()
                mMirrorMapViewFront?.mirrorLandView?.setPoints(points, selectIndex)
                mMirrorMapViewBack?.mirrorLandView?.setPoints(points, selectIndex)

            }

            override fun onTouchEvent(event: MotionEvent) {
                mLandView?.setTouchEvent(event)
                mMirrorMapViewFront?.mirrorLandView?.setTouchEvent(event)
                mMirrorMapViewBack?.mirrorLandView?.setTouchEvent(event)

                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    mMirrorMapViewFront?.mirrorLandView?.setPoints(mLandView?.points)
                    mMirrorMapViewBack?.mirrorLandView?.setPoints(mLandView?.points)
                    val point: com.esri.arcgisruntime.geometry.Point = baseMapView?.mapView
                        ?.screenToLocation(Point(event.x.toInt(), event.y.toInt()))!!
                    val viewpoint = Viewpoint(point, baseMapView?.mapView!!.mapScale)
                    mMirrorMapViewFront?.baseMapView?.setCenter(viewpoint)
                    mMirrorMapViewBack?.baseMapView?.setCenter(viewpoint)

                } else if (event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP) {
                    hideMirror()
                }
            }

            override fun onMapLongClicked(isSelect: Boolean, event: MotionEvent?) {}
            override fun onScaleEnd() {}
            override fun onSingleTapUp(isSelect: Boolean, event: MotionEvent?) {}
        })
    }

    override fun changeImageLayer() {
        if (baseMapView != null) {
            baseMapView!!.changeImageLayer()
            if (baseMapView?.isShowVector != mMirrorMapViewFront?.baseMapView?.isShowVector) {
                mMirrorMapViewFront?.baseMapView?.changeImageLayer()
            }
            if (baseMapView?.isShowVector !== mMirrorMapViewBack?.baseMapView
                    ?.isShowVector
            ) {
                mMirrorMapViewBack?.baseMapView?.changeImageLayer()
            }
        }
    }

    override fun addPoint(point: com.esri.arcgisruntime.geometry.Point?) {
        if (drawLandManager != null) {
            drawLandManager!!.add(point)
        }
    }

    override fun delete() {
        if (drawLandManager != null) {
            drawLandManager!!.delete()
        }
    }

    override fun undo() {
        if (drawLandManager != null) {
            drawLandManager!!.undo()
        }
    }

    override fun complete() {
        if (drawLandManager != null) {
            drawLandManager!!.complete()
        }
    }

    override fun startDraw(drawType: DrawType?) {
        if (drawLandManager != null) {
            drawLandManager!!.startDraw(drawType)
        }
        if (mLandView != null) {
            mLandView!!.setDrawType(drawType)
            mMirrorMapViewFront?.mirrorLandView?.setDrawType(drawType)
            mMirrorMapViewBack?.mirrorLandView?.setDrawType(drawType)

        }
    }

    override fun onPause() {
        if (baseMapView != null) {
            baseMapView!!.onPause()
        }
        mMirrorMapViewFront?.baseMapView?.onPause()
        mMirrorMapViewBack?.baseMapView?.onPause()

    }

    override fun onResume() {
        if (baseMapView != null) {
            baseMapView!!.onResume()
        }
        mMirrorMapViewFront?.baseMapView?.onResume()
        mMirrorMapViewBack?.baseMapView?.onResume()

    }

    override fun onDestroy() {
        if (baseMapView != null) {
            baseMapView!!.onDestroy()
        }
        mMirrorMapViewFront?.baseMapView?.onDestroy()
        mMirrorMapViewBack?.baseMapView?.onDestroy()

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