package com.demo.arcgis.draw.land

import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import android.text.TextUtils
import com.demo.arcgis.util.GeometryUtil
import com.esri.arcgisruntime.symbology.TextSymbol
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import android.graphics.Color
import com.esri.arcgisruntime.geometry.*
import java.util.ArrayList

/**
 * @author guoyalong
 * @time 2022/11/03
 * @desc 通过本地库获取的
 */
class BaseLand(private val mMap: MapView?) : IResultData {
    private val mGraphicsOverlay: GraphicsOverlay
    override var area: String? = null
        private set
    override var geometry: Geometry? = null
        private set

    init {
        mGraphicsOverlay = GraphicsOverlay(GraphicsOverlay.RenderingMode.STATIC)
        mMap!!.graphicsOverlays.add(mGraphicsOverlay)
    }

    /**
     * 是否选中地块
     *
     * @param point 点
     * @return 是否选中
     */
    fun selectLand(point: Point?): Boolean {
        val geometry = geometry
        return GeometryEngine.intersects(geometry, point)
    }

    /**
     * 勾画
     *
     * @param geometry 空间信息
     * @param area     面积
     */
    fun draw(geometry: Geometry?, area: String?) {
        mGraphicsOverlay.graphics.clear()
        this.geometry = geometry
        this.area = area
        drawPolygon()
        drawArea()
    }

    /**
     * 绘制面积
     */
    private fun drawArea() {
        if (TextUtils.isEmpty(area)) {
            area = GeometryUtil.getArea(geometry).toEngineeringString()
        }
        val textSymbol = TextSymbol(
            16F, area + "亩",
            Color.BLACK, TextSymbol.HorizontalAlignment.CENTER,
            TextSymbol.VerticalAlignment.MIDDLE
        )
        textSymbol.haloColor = Color.WHITE
        textSymbol.haloWidth = 1f
        val graphic = Graphic(geometry!!.extent.center, textSymbol)
        mGraphicsOverlay.graphics.add(graphic)
    }

    /**
     * 绘制面
     */
    private fun drawPolygon() {
        val lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.YELLOW, 3F)
        val fillSymbol = SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.BLACK, lineSymbol)
        fillSymbol.color = Color.argb(100, 255, 255, 255)
        val graphic = Graphic(geometry, fillSymbol)
        mGraphicsOverlay.graphics.add(graphic)
    }

    override val pointList: List<Point?>
        get() {
            val points: MutableList<Point?> = ArrayList()
            for (part in (geometry as Polygon?)!!.parts) {
                for (point in part.points) {
                    points.add(point)
                }
            }
            return points
        }

    /**
     * 删除
     */
    fun delete() {
        mMap!!.graphicsOverlays.remove(mGraphicsOverlay)
    }
}