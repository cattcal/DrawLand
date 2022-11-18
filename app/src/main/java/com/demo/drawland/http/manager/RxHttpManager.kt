package com.demo.drawland.http.manager

import android.app.Application
import com.demo.drawland.BuildConfig
import okhttp3.OkHttpClient
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.cookie.CookieStore
import rxhttp.wrapper.param.Param
import rxhttp.wrapper.ssl.HttpsUtils
import java.io.File
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

object RxHttpManager {


    fun init(context: Application) {
        val file = File(context.externalCacheDir, "RxHttpCookie")
        val sslParams = HttpsUtils.getSslSocketFactory()
        val client: OkHttpClient = OkHttpClient.Builder()
            .cookieJar(CookieStore(file))
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager) //添加信任证书
            .hostnameVerifier(HostnameVerifier { hostname: String?, session: SSLSession? -> true }) //忽略host验证
            //            .followRedirects(false)  //禁制OkHttp的重定向操作，我们自己处理重定向
            //            .addInterceptor(new RedirectInterceptor())
            //            .addInterceptor(new TokenInterceptor())
            .build()


        //设置缓存策略，非必须
//        File cacheFile = new File(context.getExternalCacheDir(), "RxHttpCache");
        //RxHttp初始化，非必须
        RxHttpPlugins.init(client) //自定义OkHttpClient对象
            .setDebug(BuildConfig.DEBUG, false, 2) //调试模式/分段打印/json数据缩进空间
            //            .setCache(cacheFile, 1000 * 100, CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE)
            //            .setExcludeCacheKeys("time")               //设置一些key，不参与cacheKey的组拼
            //            .setResultDecoder(s -> s)                  //设置数据解密/解码器，非必须
            //            .setConverter(FastJsonConverter.create())  //设置全局的转换器，非必须
            .setOnParamAssembly { p: Param<*> ->                  //设置公共参数，非必须
                //1、可根据不同请求添加不同参数，每次发送请求前都会被回调
                //2、如果希望部分请求不回调这里，发请求前调用RxHttp#setAssemblyEnabled(false)即可
                val method = p.method
                if (method.isGet) {
                    p.add("method", "get")
                } else if (method.isPost) { //Post请求
                    p.add("method", "post")
                }
                p.add("versionName", "1.0.0") //添加公共参数
                    .add("time", System.currentTimeMillis())
                    .addHeader("deviceType", "android") //添加公共请求头
            }
    }
}