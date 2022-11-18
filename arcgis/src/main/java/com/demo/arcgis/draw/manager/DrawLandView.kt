package com.demo.arcgis.draw.manager

import kotlin.jvm.JvmOverloads
import android.widget.FrameLayout
import android.content.Context
import android.util.AttributeSet

/**
 * @author guoyalong
 * @time 2022/11/03
 * @desc 地块勾画view
 */
class DrawLandView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : FrameLayout(context, attrs, defStyleAttr)