package com.demo.arcgis.draw.manager

import com.esri.arcgisruntime.mapping.view.MapView
import com.demo.arcgis.draw.land.IResultData
import android.graphics.PointF
import android.view.MotionEvent
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import com.demo.arcgis.draw.land.DrawLand
import com.demo.arcgis.draw.measure.MeasureLength
import com.demo.arcgis.draw.measure.MeasureArea
import com.demo.arcgis.draw.land.BaseLand
import com.demo.arcgis.mapview.DefaultMapViewNoRotateOnTouchListener
import android.view.ScaleGestureDetector
import android.view.View
import com.esri.arcgisruntime.geometry.*
import com.demo.arcgis.draw.manager.DrawType.*
import java.util.ArrayList

/**
 * @author guoyalong
 * @time 2022/11/03
 * @desc 地块勾画管理类
 */
class DrawLandManager @SuppressLint("ClickableViewAccessibility") constructor(
    context: Context?,
    private val mMap: MapView?
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
    private val mBaseLandList: MutableList<BaseLand>

    init {
        mDrawLandList = ArrayList()
        mMeasureLengthList = ArrayList()
        mMeasureAreaList = ArrayList()
        mBaseLandList = ArrayList()
        mMap!!.onTouchListener = object : DefaultMapViewNoRotateOnTouchListener(context, mMap) {
            override fun onLongPress(e: MotionEvent) {
                var isSelect = false
                if (mDrawType == null) {
                    val point = Point(e.x.toInt(), e.y.toInt())
                    isSelect = selectLand(mMap.screenToLocation(point))
                }
                if (mMoveListener != null) {
                    mMoveListener!!.onMapLongClicked(isSelect, e)
                }
                super.onLongPress(e)
            }

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                if (mDrawType == null) {
                    return super.onTouch(view, event)
                }
                val point = Point(event.x.toInt(), event.y.toInt())
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        when (mDrawType) {
                            DRAW_LAND -> if (editDrawLand != null) {
                                isTouch = editDrawLand!!.touchPoint(point)
                            }
                            MEASURE_LENGTH -> if (editLength != null) {
                                isTouch = editLength!!.touchPoint(point)
                            }
                            MEASURE_AREA -> if (editArea != null) {
                                isTouch = editArea!!.touchPoint(point)
                            }
                            null -> TODO()
                        }
                        isStartMove = isTouch
                        if (isStartMove) {
                            if (mMoveListener != null) {
                                mMoveListener!!.onTouchEvent(event)
                            }
                            val points: MutableList<PointF?> = ArrayList()
                            var selectIndex = -1
                            when (mDrawType) {
                                DRAW_LAND -> if (editDrawLand != null) {
                                    selectIndex = editDrawLand?.selectIndex!! - 1
                                    val pointList = editDrawLand?.pointList
                                    for (point1 in pointList!!) {
                                        val screenPoint = mMap.locationToScreen(point1)
                                        points.add(
                                            PointF(
                                                screenPoint.x.toFloat(),
                                                screenPoint.y.toFloat()
                                            )
                                        )
                                    }
                                    editDrawLand!!.isStartMove
                                }
                                MEASURE_LENGTH -> if (editLength != null) {
                                    selectIndex = editLength?.selectIndex!! - 1
                                    val pointList = editLength?.pointList
                                    for (point1 in pointList!!) {
                                        val screenPoint = mMap.locationToScreen(point1)
                                        points.add(
                                            PointF(
                                                screenPoint.x.toFloat(),
                                                screenPoint.y.toFloat()
                                            )
                                        )
                                    }
                                    editLength!!.isStartMove
                                }
                                MEASURE_AREA -> if (editArea != null) {
                                    selectIndex = editArea?.selectIndex!! - 1
                                    val pointList = editArea?.pointList
                                    for (point1 in pointList!!) {
                                        val screenPoint = mMap.locationToScreen(point1)
                                        points.add(
                                            PointF(
                                                screenPoint.x.toFloat(),
                                                screenPoint.y.toFloat()
                                            )
                                        )
                                    }
                                    editArea!!.isStartMove
                                }
                                null -> TODO()
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
                                DRAW_LAND -> if (editDrawLand != null) {
                                    editDrawLand!!.moveLand()
                                }
                                MEASURE_LENGTH -> if (editLength != null) {
                                    editLength!!.moveLand()
                                }
                                MEASURE_AREA -> if (editArea != null) {
                                    editArea!!.moveLand()
                                }
                                null -> TODO()
                            }
                            if (mMoveListener != null) {
                                mMoveListener!!.onTouchEvent(event)
                            }
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (isTouch) {
                            val endLocation = mMap.screenToLocation(point)
                            if (mDrawType != null) {
                                when (mDrawType) {
                                    DRAW_LAND -> if (editDrawLand != null) {
                                        editDrawLand!!.moveEnd(endLocation)
                                    }
                                    MEASURE_LENGTH -> if (editLength != null) {
                                        editLength!!.moveEnd(endLocation)
                                    }
                                    MEASURE_AREA -> if (editArea != null) {
                                        editArea!!.moveEnd(endLocation)
                                    }
                                    null -> TODO()
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
                return isTouch || super.onTouch(view, event)
            }

            override fun onDown(e: MotionEvent): Boolean {
                return super.onDown(e)
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val point = Point(e.x.toInt(), e.y.toInt())
                add(mMap.screenToLocation(point))
                return super.onSingleTapConfirmed(e)
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                super.onScaleEnd(detector)
                if (mMoveListener != null) {
                    mMoveListener!!.onScaleEnd()
                }
            }

            override fun onSingleTapUp(event: MotionEvent): Boolean {
                var isSelect = false
                val point = Point(event.x.toInt(), event.y.toInt())
                val location = mMap.screenToLocation(point)
                for (baseLand in mBaseLandList) {
                    isSelect = baseLand.selectLand(location)
                    if (isSelect) {
                        mBaseLandList.remove(baseLand)
                        baseLand.delete()
                        break
                    }
                }
                if (mMoveListener != null) {
                    mMoveListener!!.onSingleTapUp(isSelect, event)
                }
                return super.onSingleTapUp(event)
            }
        }
    }

    override fun startDraw(drawType: DrawType?) {
        mDrawType = drawType
        when (drawType) {
            DRAW_LAND -> {
                editDrawLand = DrawLand(mMap)
                mDrawLandList.add(editDrawLand!!)
            }
            MEASURE_LENGTH -> {
                editLength = MeasureLength(mMap)
                mMeasureLengthList.add(editLength!!)
            }
            MEASURE_AREA -> {
                editArea = MeasureArea(mMap)
                mMeasureAreaList.add(editArea!!)
            }
            null -> TODO()
        }
    }

    override val drawLands: List<DrawLand>
        get() = mDrawLandList

    override fun add(latLng: com.esri.arcgisruntime.geometry.Point?) {
        if (mDrawType == null) {
            return
        }
        when (mDrawType) {
            DRAW_LAND -> editDrawLand!!.add(latLng)
            MEASURE_LENGTH -> editLength!!.add(latLng)
            MEASURE_AREA -> editArea!!.add(latLng)
            null -> TODO()
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
            DRAW_LAND -> if (editDrawLand != null) {
                editDrawLand!!.undo()
            }
            MEASURE_LENGTH -> if (editLength != null) {
                editLength!!.undo()
            }
            MEASURE_AREA -> if (editArea != null) {
                editArea!!.undo()
            }
            null -> TODO()
        }
    }

    override fun complete() {
        if (mDrawType == null) {
            return
        }
        when (mDrawType) {
            DRAW_LAND -> {
                if (editDrawLand != null) {
                    editDrawLand!!.complete()
                }
                editDrawLand = null
            }
            MEASURE_LENGTH -> {
                if (editLength != null) {
                    editLength!!.complete()
                }
                editLength = null
            }
            MEASURE_AREA -> if (editArea != null) {
                editArea!!.complete()
            }
            else -> {}
        }
        mDrawType = null
    }

    override fun selectLand(point: com.esri.arcgisruntime.geometry.Point?): Boolean {
        if (mDrawType == null) {
            for (drawLand in mDrawLandList) {
                if (drawLand.selectLand(point)) {
                    editDrawLand = drawLand
                    mDrawType = DRAW_LAND
                    return true
                }
            }
        }
        return false
    }

    override fun touchPoint(point: Point): Boolean {
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
                DRAW_LAND -> if (editDrawLand != null) {
                    return editDrawLand!!.isUndo
                }
                MEASURE_LENGTH -> if (editLength != null) {
                    return editLength!!.isUndo
                }
                MEASURE_AREA -> if (editArea != null) {
                    return editArea!!.isUndo
                }
                null -> TODO()
            }
            return false
        }

    fun setOnMoveListener(listener: IMoveListener?) {
        mMoveListener = listener
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
        for (baseLand in mBaseLandList) {
            baseLand.delete()
        }
        mBaseLandList.clear()
        mDrawType = null
    }

    fun addBaseLand(geometry: Geometry?, area: String?) {
        val baseLand = BaseLand(mMap)
        baseLand.draw(geometry, area)
        mBaseLandList.add(baseLand)
    }

    val allDrawLand: ArrayList<IResultData>
        get() {
            val resultData = ArrayList<IResultData>()
            resultData.addAll(mDrawLandList)
            resultData.addAll(mBaseLandList)
            return resultData
        }
}