package com.demo.drawland.http

/**
 * @author: hujw
 * @time: 2022/12/7
 * @desc:
 */
class PageList<T> {
    private var curPage //当前页数
            = 0
    private var pageCount //总页数
            = 0
    private var total //总条数
            = 0
    private var datas: List<T>? = null
    fun getCurPage(): Int {
        return curPage
    }

    fun getPageCount(): Int {
        return pageCount
    }

    fun getTotal(): Int {
        return total
    }

    fun getDatas(): List<T>? {
        return datas
    }

    fun setCurPage(curPage: Int) {
        this.curPage = curPage
    }

    fun setPageCount(pageCount: Int) {
        this.pageCount = pageCount
    }

    fun setTotal(total: Int) {
        this.total = total
    }

    fun setDatas(datas: ArrayList<T>?) {
        this.datas = datas
    }
}