package com.demo.drawland.other

import android.os.Build
import timber.log.Timber

/**
 * author :hujw
 * time : 2022/11/14
 * desc : 自定义日志打印规则
 */
class DebugLoggerTree : Timber.DebugTree() {

    companion object {
        private const val MAX_TAG_LENGTH: Int = 23
    }

    /**
     * 创建日志堆栈 TAG
     */
    override fun createStackElementTag(element: StackTraceElement): String {
        val tag: String = "(" + element.fileName + ":" + element.lineNumber + ")"
        // 日志 TAG 长度限制已经在 Android 8.0 被移除
        if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tag
        }
        return tag.substring(0, MAX_TAG_LENGTH)
    }
}