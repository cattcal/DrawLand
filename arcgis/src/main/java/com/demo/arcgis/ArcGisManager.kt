package com.demo.arcgis

import android.app.Application

class ArcGisManager private constructor()  {

    companion object {

        @Suppress("StaticFieldLeak")
        private val arcGisManager: ArcGisManager by lazy { ArcGisManager() }

        fun getInstance(): ArcGisManager {
            return arcGisManager
        }

        /**
         * 获取一个对象的独立无二的标记
         */
        private fun getObjectTag(`object`: Any): String {
            // 对象所在的包名 + 对象的内存地址
            return `object`.javaClass.name + Integer.toHexString(`object`.hashCode())
        }
    }



    /** 当前应用上下文对象 */
    private lateinit var application: Application


    fun init(application: Application) {
        this.application = application
    }

    /**
     * 获取 Application 对象
     */
    fun getApplication(): Application {
        return application
    }






}
