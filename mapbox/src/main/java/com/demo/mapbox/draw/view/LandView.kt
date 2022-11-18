package com.demo.mapbox.draw.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.demo.mapbox.draw.manager.DrawType
import com.demo.mapbox.util.DensityUtil.dip2px

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
 */
class LandView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : View(context, attrs, defStyleAttr) {
    private val mOuterPointPaint: Paint
    private val mInsertPointPaint: Paint
    private val mLinePaint: Paint
    private val mPolygonPaint: Paint
    private var mSelectIndex = 0

    /**
     * 获取点集合
     *
     * @return 点集合
     */
    var points: MutableList<PointF>?
        private set
    private val mPath: Path
    private var mDrawType: DrawType? = null

    init {
        visibility = GONE
        points = ArrayList()
        // 外面的大点
        mOuterPointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mOuterPointPaint.color = Color.WHITE
        mOuterPointPaint.style = Paint.Style.FILL
        // 内部的小点
        mInsertPointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mInsertPointPaint.style = Paint.Style.FILL
        mInsertPointPaint.color = Color.RED
        // 线的颜色
        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.strokeWidth = dip2px(3f).toFloat()
        mLinePaint.color = Color.YELLOW

        // 面
        mPolygonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPolygonPaint.style = Paint.Style.FILL
        mPolygonPaint.color = Color.WHITE
        mPolygonPaint.alpha = 100
        mPath = Path()
    }

    /**
     * 设置绘制类型
     *
     * @param drawType 绘制类型
     */
    fun setDrawType(drawType: DrawType?) {
        mDrawType = drawType
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (points != null && points!!.size > 0) {
            drawLine(canvas)
            drawPoint(canvas)
        }
    }

    // 画点
    private fun drawPoint(canvas: Canvas) {
        for (i in points!!.indices) {
            val pointF = points!![i]
            canvas.drawCircle(pointF.x, pointF.y, dip2px(12f).toFloat(), mOuterPointPaint)
            if (i == mSelectIndex) {
                canvas.drawCircle(pointF.x, pointF.y, dip2px(9f).toFloat(), mInsertPointPaint)
            }
        }
    }

    // 画线
    private fun drawLine(canvas: Canvas) {
        mPath.reset()
        for (i in points!!.indices) {
            val pointF = points!![i]
            if (i == 0) {
                mPath.moveTo(pointF.x, pointF.y)
            } else {
                mPath.lineTo(pointF.x, pointF.y)
            }
        }
        if (mDrawType != DrawType.MEASURE_LENGTH) {
            mPath.close()
            canvas.drawPath(mPath, mPolygonPaint)
        }
        canvas.drawPath(mPath, mLinePaint)
    }

    fun setTouchEvent(event: MotionEvent?) {
        val pointF = PointF(event!!.x, event.y)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> visibility = VISIBLE
            MotionEvent.ACTION_MOVE -> {
                points?.removeAt(mSelectIndex)
                points?.add(mSelectIndex, pointF)
                invalidate()
            }

            MotionEvent.ACTION_UP -> visibility = GONE
        }
    }

    // 设置数据 points 点集合 selectIndex 选则的点的位置
    fun setPoints(points: MutableList<PointF>?, selectIndex: Int) {
        if (selectIndex == -1 || selectIndex >= points!!.size) {
            return
        }
        this.points = points
        mSelectIndex = selectIndex
        invalidate()
    }
}