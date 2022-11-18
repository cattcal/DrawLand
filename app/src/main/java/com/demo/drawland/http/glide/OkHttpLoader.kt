package com.demo.drawland.http.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import okhttp3.Call
import java.io.InputStream

/**
 * author :hujw
 * time : 2022/11/14
 * desc : OkHttp 加载模型
 */
class OkHttpLoader private constructor(private val factory: Call.Factory) :
    ModelLoader<GlideUrl, InputStream> {

    override fun handles(url: GlideUrl): Boolean {
        return true
    }

    override fun buildLoadData(model: GlideUrl, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream?> {
        return ModelLoader.LoadData(model, OkHttpFetcher(factory, model))
    }

    class Factory constructor(private val factory: Call.Factory) :
        ModelLoaderFactory<GlideUrl, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> {
            return OkHttpLoader(factory)
        }

        override fun teardown() {}
    }
}