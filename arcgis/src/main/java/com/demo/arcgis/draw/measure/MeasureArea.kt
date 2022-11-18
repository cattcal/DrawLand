package com.demo.arcgis.draw.measure

import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.demo.arcgis.util.GeometryUtil
import com.esri.arcgisruntime.symbology.TextSymbol
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.demo.arcgis.draw.entity.LandEntity
import com.demo.arcgis.util.DensityUtil
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.demo.arcgis.Contracts
import android.graphics.Color
import android.graphics.Point
import com.esri.arcgisruntime.geometry.*
import java.util.*

/**
 * @author guoyalong
 * @time 2022/11/03
 * @desc 面积测量
 */
class MeasureArea(private val mMap: MapView?) : IMeasure {
    private val mLandOperas: Stack<LandEntity>
    private val mGraphicsOverlay: GraphicsOverlay

    /**
     * 获取选中的下标
     *
     * @return index
     */
    var selectIndex = 0
        private set
    private val mPointList: MutableList<com.esri.arcgisruntime.geometry.Point?>

    init {
        mLandOperas = Stack()
        mGraphicsOverlay = GraphicsOverlay(GraphicsOverlay.RenderingMode.STATIC)
        mMap!!.graphicsOverlays.add(mGraphicsOverlay)
        mPointList = ArrayList()
    }

    override fun add(point: com.esri.arcgisruntime.geometry.Point?) {
        val point1 = mMap!!.locationToScreen(point)
        if (!touchPoint(point1)) {
            mLandOperas.push(LandEntity(mPointList.size, mPointList))
            mPointList.add(point)
            selectIndex = mPointList.size
            draw()
        }
    }

    override fun delete() {
        mPointList.clear()
        mLandOperas.clear()
        mMap!!.graphicsOverlays.remove(mGraphicsOverlay)
    }

    override fun undo() {
        if (isUndo) {
            val pop = mLandOperas.pop()
            mPointList.clear()
            mPointList.addAll(pop.getPoints())
            selectIndex = pop.selectPosition
            draw()
        }
    }

    override val isUndo: Boolean
        get() = !mLandOperas.isEmpty()

    override fun complete() {
        mGraphicsOverlay.graphics.clear()
        drawPolygon()
        drawArea()
    }

    override fun touchPoint(point: Point): Boolean {
        for (i in mPointList.indices) {
            val point1 = mPointList[i]
            val point2 = mMap!!.locationToScreen(point1)
            if (Math.abs(point.x - point2.x) < DensityUtil.dip2px(20f)
                && Math.abs(point.y - point2.y) < DensityUtil.dip2px(20f)
            ) {
                if (i != selectIndex - 1) { // 如果不是选中的
                    selectIndex = i + 1
                    draw()
                }
                return true
            }
        }
        return false
    }

    /**
     * 开始移动
     */
    val isStartMove: Unit
        get() {
            mLandOperas.push(LandEntity(mPointList.size, mPointList))
            mGraphicsOverlay.graphics.clear()
        }

    /**
     * 移动过程中
     */
    fun moveLand() {}

    /**
     * 移动结束
     *
     * @param point 移动的点
     */
    fun moveEnd(point: com.esri.arcgisruntime.geometry.Point?) {
        mPointList.removeAt(selectIndex - 1)
        mPointList.add(selectIndex - 1, point)
        draw()
    }

    /**
     * 勾画
     */
    private fun draw() {
        mGraphicsOverlay.graphics.clear()
        drawPolygon()
        drawPoint()
        drawArea()
    }

    /**
     * 绘制面积
     */
    private fun drawArea() {
        if (mPointList.size < 3) {
            return
        }
        val geometry = geometry
        val textSymbol = TextSymbol(
            16F,
            GeometryUtil.getArea(geometry)
                .toEngineeringString() + "亩", Color.BLACK,
            TextSymbol.HorizontalAlignment.CENTER,
            TextSymbol.VerticalAlignment.MIDDLE
        )
        textSymbol.haloColor = Color.WHITE
        textSymbol.haloWidth = 1f
        val graphic = Graphic(geometry.extent.center, textSymbol)
        mGraphicsOverlay.graphics.add(graphic)
    }

    /**
     * 绘制点
     */
    private fun drawPoint() {
        val markerSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 20F)
        val blackOutline = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.WHITE, 3F)
        markerSymbol.outline = blackOutline
        val markerSymbol1 = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.WHITE, 20F)
        markerSymbol1.outline = blackOutline
        for (i in mPointList.indices) {
            var graphic: Graphic
            graphic = if (i == selectIndex - 1) {
                Graphic(mPointList[i], markerSymbol)
            } else {
                Graphic(mPointList[i], markerSymbol1)
            }
            mGraphicsOverlay.graphics.add(graphic)
        }
    }

    /**
     * 绘制面
     */
    private fun drawPolygon() {
        val geometry = geometry
        val lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.YELLOW, 3F)
        val fillSymbol = SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.BLACK, lineSymbol)
        fillSymbol.color = Color.argb(100, 255, 255, 255)
        val graphic = Graphic(geometry, fillSymbol)
        mGraphicsOverlay.graphics.add(graphic)
    }

    /**
     * 空间信息
     *
     * @return geometry
     */
    private val geometry: Geometry
        private get() {
            val pointCollection = PointCollection(mPointList)
            return Polygon(pointCollection)
        }

    /**
     * 获取点集合
     *
     * @return 点集合
     */
    val pointList: List<com.esri.arcgisruntime.geometry.Point?>
        get() = mPointList
}