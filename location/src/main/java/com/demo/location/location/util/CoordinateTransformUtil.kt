package com.demo.location.location.util

/**
 * @author guoyalong
 * @time 2022/11/08
 * @desc 坐标转换将 国测局02坐标转成wgs 84坐标
 */
// 坐标转换工具类
object CoordinateTransformUtil {
    private const val PI = 3.14159265358979324
    private const val EARTH_RADIUS = 6378137.0
    private const val EE = 0.00669342162296594323

    /**
     * gcj02 转 wgs84
     *
     * @param lat 纬度
     * @param lon 经度
     * @return double[] latitude, longitude
     */
    fun transformLocationFromGcj02ToWgs84(lat: Double, lon: Double): DoubleArray {
        val transforms = transform(lat, lon)
        val longitude = lon * 2 - transforms[1]
        val latitude = lat * 2 - transforms[0]
        return doubleArrayOf(latitude, longitude)
    }

    /**
     * wgs84 转 gcj02
     *
     * @param lat 纬度
     * @param lon 经度
     * @return double[] latitude, longitude
     */
    fun transformLocationFromWgs84ToGcj02(lat: Double, lon: Double): DoubleArray {
        if (outOfChina(lat, lon)) {
            return doubleArrayOf(lat, lon)
        }
        var transLat = transformLat(lon - 105.0, lat - 35.0)
        var transLon = transformLon(lon - 105.0, lat - 35.0)
        val radLat = lat / 180.0 * PI
        var magic = Math.sin(radLat)
        magic = 1 - EE * magic * magic
        val sqrtMagic = Math.sqrt(magic)
        transLat = transLat * 180.0 / (EARTH_RADIUS * (1 - EE) / (magic * sqrtMagic) * PI)
        transLon = transLon * 180.0 / (EARTH_RADIUS / sqrtMagic * Math.cos(radLat) * PI)
        val mgLat = lat + transLat
        val mgLon = lon + transLon
        return doubleArrayOf(mgLat, mgLon)
    }

    private fun transform(lat: Double, lon: Double): DoubleArray {
        if (outOfChina(lat, lon)) {
            return doubleArrayOf(lat, lon)
        }
        var transLat = transformLat(lon - 105.0, lat - 35.0)
        var transLon = transformLon(lon - 105.0, lat - 35.0)
        val radLat = lat / 180.0 * PI
        var magic = Math.sin(radLat)
        magic = 1 - EE * magic * magic
        val sqrtMagic = Math.sqrt(magic)
        transLat = transLat * 180.0 / (EARTH_RADIUS * (1 - EE) / (magic * sqrtMagic) * PI)
        transLon = transLon * 180.0 / (EARTH_RADIUS / sqrtMagic * Math.cos(radLat) * PI)
        val mgLat = lat + transLat
        val mgLon = lon + transLon
        return doubleArrayOf(mgLat, mgLon)
    }

    /**
     * 是否超出中国
     *
     * @param lat 纬度
     * @param lon 经度
     * @return 超出中国true or false
     */
    fun outOfChina(lat: Double, lon: Double): Boolean {
        return if (lon < 72.004 || lon > 137.8347) {
            true
        } else lat < 0.8293 || lat > 55.8271
    }

    private fun transformLat(lon: Double, lat: Double): Double {
        var ret =
            -100.0 + 2.0 * lon + 3.0 * lat + 0.2 * lat * lat + 0.1 * lon * lat + 0.2 * Math.sqrt(
                Math.abs(lon)
            )
        ret += (20.0 * Math.sin(6.0 * lon * PI) + 20.0 * Math.sin(2.0 * lon * PI)) * 2.0 / 3.0
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0
        return ret
    }

    private fun transformLon(lon: Double, lat: Double): Double {
        var ret = 300.0 + lon + 2.0 * lat + 0.1 * lon * lon + 0.1 * lon * lat + 0.1 * Math.sqrt(
            Math.abs(lon)
        )
        ret += (20.0 * Math.sin(6.0 * lon * PI) + 20.0 * Math.sin(2.0 * lon * PI)) * 2.0 / 3.0
        ret += (20.0 * Math.sin(lon * PI) + 40.0 * Math.sin(lon / 3.0 * PI)) * 2.0 / 3.0
        ret += (150.0 * Math.sin(lon / 12.0 * PI) + 300.0 * Math.sin(lon / 30.0 * PI)) * 2.0 / 3.0
        return ret
    }
}