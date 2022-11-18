package com.demo.arcgis.mapview.layer.cache

import android.content.Context
import android.net.ConnectivityManager
import android.os.Environment
import android.util.Log
import com.esri.arcgisruntime.arcgisservices.TileInfo
import com.esri.arcgisruntime.data.TileKey
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.layers.ImageTiledLayer
import com.esri.arcgisruntime.layers.WebTiledLayer
import com.esri.arcgisruntime.mapping.Basemap
import com.demo.arcgis.ArcGisManager.Companion.getInstance
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Objects

class MapBoxCacheTiledLayer private constructor(//地图服务地址
    private val mUrl: String, tileInfo: TileInfo, envelope: Envelope
) :
    ImageTiledLayer(tileInfo, envelope) {
    init {
        CACHE_PATH =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + "xdzt" + File.separator + "base_cache" + File.separator + "Mapbox缓存底图"
        val file = File(CACHE_PATH)
        if (!file.exists()) { //判断文件目录是否存在
            file.mkdirs()
        }
        //创建隐藏文件，放置图片进入手机图库
        val nomediaFile = File(file.absolutePath, ".nomedia")
        if (!nomediaFile.exists()) {
            try {
                nomediaFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun getTile(tileKey: TileKey): ByteArray? {
        val level = tileKey.level
        val col = tileKey.column
        val row = tileKey.row
        // 若缓存存在则返回缓存字节
        val fileName = col.toString() + "_" + row + ".png"
        if (hasCached(level.toString() + "", fileName)) {
            //本地存在
            val _cacheLevelDir = File(CACHE_PATH + "/" + level)
            var cachedBytes: ByteArray? = null
            for (_file in Objects.requireNonNull(_cacheLevelDir.listFiles())) {
                if (_file.name == fileName) {
                    cachedBytes = ByteArray(_file.length().toInt())
                    try {
                        val _fis = FileInputStream(_file)
                        _fis.read(cachedBytes)
                        _fis.close()
                        return cachedBytes
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        //本地不存在，从网络加载
        return if (isNetworkConnected) {
            downloadData(level, row, col)!!
        } else null
    }

    /**
     * 判断是否有缓存数据
     *
     * @param level    级别
     * @param fileName 瓦片名称  row_col.png
     * @return
     */
    private fun hasCached(level: String, fileName: String): Boolean {
        val _cacheLevelDir = File(CACHE_PATH + "/" + level)
        if (!_cacheLevelDir.exists()) {
            _cacheLevelDir.mkdir()
            return false
        }
        for (_file in Objects.requireNonNull(_cacheLevelDir.listFiles())) {
            if (_file.name == fileName) { //存在同名文件
                return try {
                    val cachedBytes = ByteArray(_file.length().toInt())
                    val _fis = FileInputStream(_file)
                    _fis.read(cachedBytes) //文件可读
                    _fis.close()
                    true
                } catch (e: Exception) {
                    //文件不可读
                    false
                }
            }
        }
        //不存在同名文件
        return false
    }

    /**
     * 从网络加载瓦片，并保存本地
     *
     * @param level 级别
     * @param row   行
     * @param col   列
     * @return 瓦片数据
     *
     *
     * "https://api.mapbox.com/styles/v1/tanqidi/ckxehcket2c5214nmxkyar1xa/tiles/256/{level}/{col}/{row}@2x?access_token=pk.eyJ1IjoidGFucWlkaSIsImEiOiJja2I5ZDZzNnQwYmhwMnJwZjEzYjc0emJxIn0.dBbJo1l2tqhBADkEP1GKpw"
     */
    private fun downloadData(level: Int, row: Int, col: Int): ByteArray? {
        var result: ByteArray? = null
        try {
            val newUrl = mUrl.replace("{level}", level.toString()).replace("{col}", col.toString())
                .replace("{row}", row.toString())
            val url = URL(newUrl)
            val buf = ByteArray(1024)
            val httpConnection = url.openConnection() as HttpURLConnection
            httpConnection.connect()
            val `is` = BufferedInputStream(httpConnection.inputStream)
            val bos = ByteArrayOutputStream()
            var temp = -1
            while (`is`.read(buf).also { temp = it } > 0) {
                bos.write(buf, 0, temp)
            }
            `is`.close()
            httpConnection.disconnect()
            result = bos.toByteArray()
            saveMapCache(result, col.toString() + "_" + row + ".png", level.toString() + "")
        } catch (e: Exception) {
            //e.printStackTrace();
        }
        return result
    }

    /**
     * 保存瓦片本地
     *
     * @param bytes    瓦片数据
     * @param fileName 瓦片命名
     * @param level    级别
     */
    private fun saveMapCache(bytes: ByteArray?, fileName: String, level: String) {
        try {
            val _cacheDir = File(CACHE_PATH)
            if (!_cacheDir.exists()) {
                _cacheDir.mkdir()
            }
            val _cacheLevelDir = File(CACHE_PATH + "/" + level)
            if (!_cacheLevelDir.exists()) {
                _cacheLevelDir.mkdir()
            }
            val _cacheFile = File(_cacheLevelDir, fileName)
            _cacheFile.createNewFile()
            val _fos = FileOutputStream(_cacheFile)
            _fos.write(bytes)
            _fos.close()
        } catch (e: IOException) {
            Log.e("mapCache", "saveMapCache：$e")
        }
    }

    companion object {
        private lateinit var CACHE_PATH //缓存路径
                : String

        /**
         * @param url 地图地址
         * @return Basemap
         */
        fun createBaseMap(url: String): Basemap {
            val webTiledLayer = WebTiledLayer(url)
            val fullExtent = webTiledLayer.fullExtent
            val basemap = Basemap()
            val tileInfo = webTiledLayer.tileInfo
            val cacheTiledLayer = MapBoxCacheTiledLayer(url, tileInfo, fullExtent)
            cacheTiledLayer.isVisible=true
            basemap.baseLayers.add(cacheTiledLayer)
            return basemap
        }

        /**
         * 是否有网络
         * 需要权限 ACCESS_NETWORK_STATE
         *
         * @return true  or   false
         */
        val isNetworkConnected: Boolean
            get() {
                val mConnectivityManager = getInstance().getApplication()
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mNetworkInfo = mConnectivityManager.activeNetworkInfo
                return mNetworkInfo?.isAvailable ?: false
            }
    }
}