package com.demo.mapbox.util

import android.annotation.SuppressLint
import android.text.TextUtils
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import java.util.regex.Pattern

/**
 * @author: hujw
 * @time: 2022/11/16
 * @desc:
 */
object NumberFormatUtil {
    /**
     * 将double类型数据转换为百分比格式，并保留小数点前IntegerDigits位和小数点后FractionDigits位
     *
     * @param num            待转换浮点数
     * @param integerDigits  小数点前保留几位
     * @param fractionDigits 小数点后保留几位
     * @return 百分比.
     */
    fun getPercentFormat(num: Double, integerDigits: Int, fractionDigits: Int): String {
        val nf = NumberFormat.getPercentInstance()
        nf.maximumIntegerDigits = integerDigits //小数点前保留几位
        nf.minimumFractionDigits = fractionDigits // 小数点后保留几位
        return nf.format(num)
    }

    /**
     * 将double类型数据转换为字符串，保留指定位数的小数。
     * 如果keepFractionTailZeros为false，则删除小数尾部的0.
     *
     * @param num                   num
     * @param fractionDigits        dia
     * @param keepFractionTailZeros 0
     * @return string
     */
    fun doubleToFixedFractionDigitsString(
        num: Double,
        fractionDigits: Int,
        keepFractionTailZeros: Boolean
    ): String {
        val str = String.format(Locale.CHINA, "%." + fractionDigits + "f", num)
        var result = str
        val dotIndex = str.indexOf('.')
        if (dotIndex >= 0 && !keepFractionTailZeros) {
            for (i in str.length - 1 downTo dotIndex) {
                if ('.' == str[i]) {
                    result = str.substring(0, i)
                    break
                } else if (str[i] != '0') {
                    result = str.substring(0, i + 1)
                    break
                }
            }
        }
        return result
    }

    /**
     * 通过字符串查询电话
     *
     * @param value 字符串
     * @return 电话
     */
    fun queryPhoneNumber(value: String): String {
        if (value.length <= 0) {
            return ""
        }
        val pattern = Pattern.compile("(\\(?\\d+\\)?\\-?)?\\d+(\\-?\\d+)*")
        //        String regex = "^((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(18[0-9])|(17[0-9]))\\d{8}$";
        val regex = "^(1[3-9])\\d{9}$"
        val pattern1 = Pattern.compile(regex)
        val matcher = pattern.matcher(value)
        while (matcher.find()) {
            val m = matcher.group()
            val matcher2 = pattern1.matcher(m)
            while (matcher2.find()) {
                return matcher2.group()
            }
        }
        return ""
    }

    /**
     * 有效国内电话判断.
     *
     * @param phone tel
     * @return bool
     */
    fun isValidPhone(phone: String?): Boolean {
        if (null == phone || "" == phone) {
            return false
        }
        val length = phone.length
        val ptn = Pattern.compile(
            "^((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(18[0-9])|(17[0-9]))\\d{8}$"
        )
        val matcher = ptn.matcher(phone)
        return 11 == length && matcher.matches()
    }

    /**
     * 将double类型的数据进转化，如果以.0结束则去掉
     * 例如6.0返回6，6.01返回6.01
     *
     * @param number number
     * @return string
     */
    fun doubleRemoveEndZero(number: Double): String {
        val decimalFormat = DecimalFormat("###################.###########")
        return decimalFormat.format(number)
    }

    /**
     * 将String类型的数据进转化，如果以.0结束则去掉
     * 例如6.0返回6，6.01返回6.01
     *
     * @param number number
     * @return string
     */
    @JvmStatic
    fun doubleRemoveEndZero(number: String): String {
        if (TextUtils.isEmpty(number)) {
            return "0"
        }
        val str = pointTwo(number)
        return doubleRemoveEndZero(str.toDouble())
    }

    /**
     * 保留两位小数
     *
     * @param number number
     * @return number
     */
    fun pointTwo(number: Double): String {
        val df = DecimalFormat("######.00")
        val string = df.format(number)
        return if ("." == string.substring(0, 1)) {
            "0$string"
        } else string
    }

    /**
     * 保留两位小数
     *
     * @param number number
     * @return number
     */
    fun pointTwo(number: String): String {
        if (TextUtils.isEmpty(number)) {
            return "0.00"
        }
        @SuppressLint("UseValueOf") val value = number.toDouble()
        val df = DecimalFormat("######.00")
        val string = df.format(value)
        return if ("." == string.substring(0, 1)) {
            "0" + df.format(value)
        } else df.format(value)
    }

    /**
     * 保留两位小数
     *
     * @param number number
     * @return 小数
     */
    fun retainTwoDecimal(number: Any?): String {
        val decimalFormat = DecimalFormat("#.00")
        val format = decimalFormat.format(number)
        val bigDecimal = BigDecimal(format)
        return bigDecimal.toEngineeringString()
    }

    /**
     * 科学计数法
     *
     * @param number number
     * @return string
     */
    fun countMethod(number: Double): String {
        return if (number < 10000) {
            doubleRemoveEndZero(pointTwo(number).toDouble())
        } else if (number < 100000000) {
            doubleRemoveEndZero(
                pointTwo(number / 10000).toDouble()
            ) + "万"
        } else {
            doubleRemoveEndZero(
                pointTwo(number / 100000000).toDouble()
            ) + "亿"
        }
    }

    /**
     * 保留六位小数
     *
     * @param number number
     * @return number
     */
    fun pointSix(number: Double): String {
        val df = DecimalFormat("######.000000")
        val string = df.format(number)
        return if ("." == string.substring(0, 1)) {
            "0$string"
        } else string
    }

    /**
     * 保存8位小数
     *
     * @param number number
     * @return number
     */
    fun pointEight(number: Double): String {
        val df = DecimalFormat("######.00000000")
        val string = df.format(number)
        return if ("." == string.substring(0, 1)) {
            "0$string"
        } else string
    }
}