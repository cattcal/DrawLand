package com.demo.arcgis.draw.manager

import com.demo.arcgis.draw.land.IDrawLand
import com.demo.arcgis.draw.land.DrawLand
import com.demo.arcgis.draw.manager.DrawType

/**
 * @author guoyalong
 * 地块勾画 管理类 接口
 */
interface IDrawLandManager : IDrawLand {
    /**
     * 开始勾画
     *
     * @param drawType 勾画方式
     */
    fun startDraw(drawType: DrawType?)

    /**
     * 获取地块勾画的集合
     *
     * @return drawLands
     */
    val drawLands: List<DrawLand>

    /**
     * 获取正在编辑的地块
     *
     * @return DrawLand
     */
    val editDrawLand: DrawLand?
}