package com.demo.mapbox.draw.manager

import com.demo.mapbox.draw.land.DrawLand
import com.demo.mapbox.draw.land.IDrawLand

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
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