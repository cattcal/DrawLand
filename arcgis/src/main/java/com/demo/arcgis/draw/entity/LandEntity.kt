package com.demo.arcgis.draw.entity

import com.esri.arcgisruntime.geometry.*
import java.util.ArrayList

/**
 * @author guoyalong
 * @time 2022/11/03
 * @desc 每次操作 entity
 */
class LandEntity(val selectPosition: Int, points: List<Point?>?) {
    private val points: MutableList<Point?>

    init {
        this.points = ArrayList()
        this.points.addAll(points!!)
    }

    fun getPoints(): List<Point?> {
        return points
    }
}