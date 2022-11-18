package com.demo.mapbox.draw.manager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import com.mapbox.mapboxsdk.geometry.LatLng
import com.demo.mapbox.draw.land.DrawLand
import com.demo.mapbox.draw.measure.MeasureArea
import com.demo.mapbox.draw.measure.MeasureLength
import com.demo.mapbox.mapview.BaseMapView
import java.util.UUID

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
 */
class DrawLandManager @SuppressLint("ClickableViewAccessibility") constructor(
    context: Context?,
    private val mMapView: BaseMapView
) : IDrawLandManager {
    private val mDrawLandList: MutableList<DrawLand>
    private var mDrawType: DrawType? = null
    override var editDrawLand: DrawLand? = null
    private var isTouch = false
    private var isStartMove = false
    private var mMoveListener: IMoveListener? = null
    private val mMeasureLengthList: MutableList<MeasureLength>
    var editLength: MeasureLength? = null
    private val mMeasureAreaList: MutableList<MeasureArea>
    var editArea: MeasureArea? = null

    init {
        mDrawLandList = ArrayList()
        mMeasureLengthList = ArrayList()
        mMeasureAreaList = ArrayList()
    }

    /**
     * 添加触摸事件
     */
    @SuppressLint("ClickableViewAccessibility")
    fun addTouchListener() {
        mMapView.setOnTouchListener { v: View?, event: MotionEvent ->
            if (mDrawType == null) {
                return@setOnTouchListener false
            }
            val pointF = PointF(event.x, event.y)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    when (mDrawType) {
                        DrawType.DRAW_LAND -> if (editDrawLand != null) {
                            isTouch = editDrawLand!!.touchPoint(pointF)
                        }

                        DrawType.MEASURE_LENGTH -> if (editLength != null) {
                            isTouch = editLength!!.touchPoint(pointF)
                        }

                        DrawType.MEASURE_AREA -> if (editArea != null) {
                            isTouch = editArea!!.touchPoint(pointF)
                        }

                        else -> {}
                    }
                    isStartMove = isTouch
                    if (isStartMove) {
                        if (mMoveListener != null) {
                            mMoveListener!!.onTouchEvent(event)
                        }
                        val points: MutableList<PointF> = ArrayList()
                        var selectIndex = -1
                        when (mDrawType) {
                            DrawType.DRAW_LAND -> if (editDrawLand != null) {
                                selectIndex = editDrawLand!!.selectIndex - 1
                                val pointList = editDrawLand!!.pointList
                                for (point1 in pointList) {
                                    val screenPoint = mMapView.map!!.projection.toScreenLocation(
                                        point1
                                    )
                                    points.add(screenPoint)
                                }
                                editDrawLand!!.isStartMove
                            }

                            DrawType.MEASURE_LENGTH -> if (editLength != null) {
                                selectIndex = editLength!!.selectIndex - 1
                                val pointList = editLength!!.pointList
                                for (point1 in pointList) {
                                    val screenPoint = mMapView.map!!.projection.toScreenLocation(
                                        point1!!
                                    )
                                    points.add(screenPoint)
                                }
                                editLength!!.isStartMove
                            }

                            DrawType.MEASURE_AREA -> if (editArea != null) {
                                selectIndex = editArea!!.selectIndex - 1
                                val pointList = editArea!!.pointList
                                for (point1 in pointList) {
                                    val screenPoint = mMapView.map!!.projection.toScreenLocation(
                                        point1!!
                                    )
                                    points.add(screenPoint)
                                }
                                editArea!!.isStartMove
                            }

                            else -> {}
                        }
                        if (mMoveListener != null && selectIndex > -1) {
                            mMoveListener!!.startMove(event, points, selectIndex)
                        }
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    if (isStartMove) {
                        isStartMove = false
                    }
                    if (isTouch) {
                        when (mDrawType) {
                            DrawType.DRAW_LAND -> if (editDrawLand != null) {
                                editDrawLand!!.moveLand()
                            }

                            DrawType.MEASURE_LENGTH -> if (editLength != null) {
                                editLength!!.moveLand()
                            }

                            DrawType.MEASURE_AREA -> if (editArea != null) {
                                editArea!!.moveLand()
                            }

                            else -> {}
                        }
                        if (mMoveListener != null) {
                            mMoveListener!!.onTouchEvent(event)
                        }
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (isTouch) {
                        val endLocation = mMapView.map!!.projection.fromScreenLocation(pointF)
                        if (mDrawType != null) {
                            when (mDrawType) {
                                DrawType.DRAW_LAND -> if (editDrawLand != null) {
                                    editDrawLand!!.moveEnd(endLocation)
                                }

                                DrawType.MEASURE_LENGTH -> if (editLength != null) {
                                    editLength!!.moveEnd(endLocation)
                                }

                                DrawType.MEASURE_AREA -> if (editArea != null) {
                                    editArea!!.moveEnd(endLocation)
                                }

                                else -> {}
                            }
                        }
                        if (mMoveListener != null) {
                            mMoveListener!!.onTouchEvent(event)
                        }
                    }
                    isTouch = false
                    isStartMove = false
                }
            }
            isTouch
        }
    }

    /**
     * 设置底图点击监听
     */
    fun addMapClickListener() {
        mMapView.map!!.addOnMapClickListener { point: LatLng ->
            add(point)
            false
        }
    }

    /**
     * 设置底图长按监听
     */
    fun addMapLongClickListener() {
        mMapView.map!!.addOnMapLongClickListener { point: LatLng? ->
            var isSelect = false
            if (mDrawType == null) {
                isSelect = selectLand(point)
            }
            if (mMoveListener != null) {
                mMoveListener!!.onMapLongClicked(isSelect)
            }
            false
        }
    }

    override fun startDraw(drawType: DrawType?) {
        mDrawType = drawType
        when (drawType) {
            DrawType.DRAW_LAND -> {
                editDrawLand = DrawLand(mMapView.map!!, UUID.randomUUID().toString())
                mDrawLandList.add(editDrawLand!!)
            }

            DrawType.MEASURE_LENGTH -> {
                editLength = MeasureLength(mMapView.map, UUID.randomUUID().toString())
                mMeasureLengthList.add(editLength!!)
            }

            DrawType.MEASURE_AREA -> {
                editArea = MeasureArea(mMapView.map, UUID.randomUUID().toString())
                mMeasureAreaList.add(editArea!!)
            }

            else -> {}
        }
    }

    override val drawLands: List<DrawLand>
        get() = mDrawLandList

    override fun add(latLng: LatLng?) {
        if (mDrawType == null) {
            return
        }
        when (mDrawType) {
            DrawType.DRAW_LAND -> editDrawLand!!.add(latLng)
            DrawType.MEASURE_LENGTH -> editLength!!.add(latLng)
            DrawType.MEASURE_AREA -> editArea!!.add(latLng)
            else -> {}
        }
    }

    override fun delete() {
        if (editDrawLand != null) {
            mDrawLandList.remove(editDrawLand)
            editDrawLand!!.delete()
            editDrawLand = null
        }
        if (editLength != null) {
            mMeasureLengthList.remove(editLength)
            editLength!!.delete()
            editLength = null
        }
        for (length in mMeasureLengthList) {
            length.delete()
        }
        mMeasureLengthList.clear()
        if (editArea != null) {
            mMeasureAreaList.remove(editArea)
            editArea!!.delete()
            editArea = null
        }
        for (area in mMeasureAreaList) {
            area.delete()
        }
        mMeasureAreaList.clear()
        mDrawType = null
    }

    override fun undo() {
        if (mDrawType == null) {
            return
        }
        when (mDrawType) {
            DrawType.DRAW_LAND -> if (editDrawLand != null) {
                editDrawLand!!.undo()
            }

            DrawType.MEASURE_LENGTH -> if (editLength != null) {
                editLength!!.undo()
            }

            DrawType.MEASURE_AREA -> if (editArea != null) {
                editArea!!.undo()
            }

            else -> {}
        }
    }

    override fun complete() {
        if (mDrawType == null) {
            return
        }
        when (mDrawType) {
            DrawType.DRAW_LAND -> {
                if (editDrawLand != null) {
                    editDrawLand!!.complete()
                }
                editDrawLand = null
            }

            DrawType.MEASURE_LENGTH -> {
                if (editLength != null) {
                    editLength!!.complete()
                }
                editLength = null
            }

            DrawType.MEASURE_AREA -> if (editArea != null) {
                editArea!!.complete()
            }

            else -> {}
        }
        mDrawType = null
    }

    override fun selectLand(point: LatLng?): Boolean {
        if (mDrawType == null) {
            for (drawLand in mDrawLandList) {
                if (drawLand.selectLand(point)) {
                    editDrawLand = drawLand
                    mDrawType = DrawType.DRAW_LAND
                    return true
                }
            }
        }
        return false
    }

    override fun touchPoint(point: PointF): Boolean {
        for (drawLand in mDrawLandList) {
            if (drawLand.touchPoint(point)) {
                return true
            }
        }
        return false
    }

    override val isUndo: Boolean
        get() {
            if (mDrawType == null) {
                return false
            }
            when (mDrawType) {
                DrawType.DRAW_LAND -> if (editDrawLand != null) {
                    return editDrawLand!!.isUndo
                }

                DrawType.MEASURE_LENGTH -> if (editLength != null) {
                    return editLength!!.isUndo
                }

                DrawType.MEASURE_AREA -> if (editArea != null) {
                    return editArea!!.isUndo
                }

                else -> {}
            }
            return false
        }

    fun cleanAll() {
        if (editDrawLand != null) {
            mDrawLandList.remove(editDrawLand)
            editDrawLand!!.delete()
            editDrawLand = null
        }
        for (drawLand in mDrawLandList) {
            drawLand.delete()
        }
        mDrawLandList.clear()
        if (editLength != null) {
            mMeasureLengthList.remove(editLength)
            editLength!!.delete()
            editLength = null
        }
        for (length in mMeasureLengthList) {
            length.delete()
        }
        mMeasureLengthList.clear()
        if (editArea != null) {
            mMeasureAreaList.remove(editArea)
            editArea!!.delete()
            editArea = null
        }
        for (area in mMeasureAreaList) {
            area.delete()
        }
        mMeasureAreaList.clear()
        mDrawType = null
    }

    /**
     * 设置移动监听
     *
     * @param listener listener
     */
    fun setOnMoveListener(listener: IMoveListener?) {
        mMoveListener = listener
    }
}