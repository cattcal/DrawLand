package com.demo.mapbox.util

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset

/**
 * @author: hujw
 * @time: 2022/11/17
 * @desc:
 */
object AssetsUtil {

    fun loadStringFromAssets(context: Context?, fileName: String): String? {
        return try {
            val inputStream = context?.assets?.open(fileName)
            val rd = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
            val sb = StringBuilder()
            rd.forEachLine {
                sb.append(it)
            }
            sb.toString()
        } catch (e: IOException) {
//            logE("TAG", "Unable to parse $fileName")
            null
        }
    }
}