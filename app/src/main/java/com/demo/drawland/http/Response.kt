package com.demo.drawland.http

/**
 * @author: hujw
 * @time: 2022/12/7
 * @desc:
 */
class Response<T> {
    var errorCode = 0
    var errorMsg: String? = null
    var data: T? = null
}
