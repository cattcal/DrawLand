package com.demo.location.location

import android.content.Context
import com.demo.location.location.util.CoordinateTransformUtil
import com.amap.api.location.DPoint
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.demo.location.R
import com.demo.location.location.transform.Transform
import java.lang.Exception
import java.lang.NullPointerException

/**
 * @author guoyalong
 * @time 2022/11/08
 * @desc 坐标转换将 国测局02坐标转成wgs 84坐标
 */
// 具体定位方法
class LocationOperations(context: Context?) : ILocationOperation {
    private var mLocationClient: AMapLocationClient? = null
    private val mContext: Context
    private var mLocationListener: ILocationListener? = null
    private var mIsStop = true
    private var mStartLocationTime: Long = 0
    private var mTransform: Transform? = null
    private val mOption: AMapLocationClientOption
    private fun setDefaultOption() {
        mOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        mOption.isGpsFirst = false //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.httpTimeOut = 30000 //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.interval = 2000 //可选，设置定位间隔。默认为2秒
        mOption.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Transport
        mOption.isNeedAddress = true //可选，设置是否返回逆地理地址信息。默认是ture
        mOption.isOnceLocation = false //可选，设置是否单次定位。默认是false
    }

    override fun start() {
        mIsStop = false
        mStartLocationTime = System.currentTimeMillis()
        if (mLocationClient == null) {
            initLocation()
        }
        mLocationClient?.startLocation()
    }

    private fun initLocation() {
        //初始化client
        try {
            AMapLocationClient.updatePrivacyShow(mContext, true, true)
            AMapLocationClient.updatePrivacyAgree(mContext, true)
            mLocationClient = AMapLocationClient(mContext)
            //设置定位参数
            mLocationClient?.setLocationOption(mOption)
            // 设置定位监听
            mLocationClient?.setLocationListener(mMapLocationListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val mMapLocationListener = AMapLocationListener { location ->
        if (location != null) {
            if (location.errorCode == 0) {
                if (mStartLocationTime == 0L) {
                    if (mLocationListener != null) {
                        mLocationListener?.onLocationFailure(context?.getString(R.string.location_failure))
                    }
                    return@AMapLocationListener
                }
                if (CoordinateTransformUtil.outOfChina(location.latitude, location.longitude)) {
                    mLocationListener?.onLocationFailure(context?.getString(R.string.out_of_china))
                    return@AMapLocationListener
                }
                locationChange(location)
            } else {
                if (mLocationListener != null) {
                    mLocationListener?.onLocationFailure(location.errorInfo)
                }
            }
        } else {
            if (mLocationListener != null) {
                mLocationListener?.onLocationFailure(context?.getString(R.string.location_failure))
            }
        }
    }

    init {
        if (context == null) {
            throw NullPointerException("parameter is null")
        }
        mContext = context.applicationContext
        mOption = AMapLocationClientOption()
        setDefaultOption()
    }

    private fun locationChange(location: AMapLocation) {
        mStartLocationTime = System.currentTimeMillis()
        location.time = mStartLocationTime
        var result: DPoint? = DPoint(location.latitude, location.longitude)
        if (mTransform != null) {
            result = mTransform?.transform(result)
        }
        if (result != null) {
            location.longitude = result.longitude
            location.latitude = result.latitude
        }
        val loc: ILocation = BaseLocation(location)
        if (mLocationListener != null && !mIsStop) {
            mLocationListener?.onLocationSuccess(loc)
        }
    }

    override fun stop() {
        mIsStop = true
        mStartLocationTime = 0
        if (mLocationClient != null && mLocationClient!!.isStarted) {
            mLocationClient?.stopLocation()
        }
    }

    override fun destroy() {
        if (null != mLocationClient) {
            stop()
            mLocationClient?.onDestroy()
            mLocationClient = null
            mLocationListener = null
        }
    }

    override val isStart: Boolean
        get() = !mIsStop

    override fun setLocationListener(locationListener: ILocationListener?) {
        mLocationListener = locationListener
    }

    /**
     * 设置间隔时间，必须在[LocationOperations.start]之前执行
     *
     * @param time long
     */
    fun setInterval(time: Long) {
        mOption.interval = time
    }

    /**
     * 设置坐标转换算法
     *
     * @param transform Transform 接口
     */
    fun setTransform(transform: Transform?) {
        mTransform = transform
    }
}