package com.demo.drawland.http.bean

/**
 * @author: hujw
 * @time: 2022/12/7
 * @desc:
 */
class Article {
    /**
     * apkLink :
     * audit : 1
     * author :
     * chapterId : 76
     * chapterName : 项目架构
     * collect : false
     * courseId : 13
     * desc :
     * envelopePic :
     * fresh : true
     * id : 9300
     * link : https://mp.weixin.qq.com/s/_6p6vfce7m5E8AwGDg2cZg
     * niceDate : 10小时前
     * niceShareDate : 11小时前
     * origin :
     * prefix :
     * projectLink :
     * publishTime : 1569255115000
     * shareDate : 1569249942000
     * shareUser : ZYLAB
     * superChapterId : 74
     * superChapterName : 热门专题
     * tags : []
     * title : Android 开发中的架构模式 -- MVC / MVP / MVVM
     * type : 0
     * userId : 10577
     * visible : 1
     * zan : 0
     */
    var apkLink: String? = null
    var audit = 0
    var author: String? = null
    var chapterId = 0
    var chapterName: String? = null
    var collect = false
    var courseId = 0
    var desc: String? = null
    var envelopePic: String? = null
    var fresh = false
    var id = 0
    var link: String? = null
    var niceDate: String? = null
    var niceShareDate: String? = null
    var origin: String? = null
    var prefix: String? = null
    var projectLink: String? = null
    var publishTime: Long = 0
    var shareDate: Long = 0
    var shareUser: String? = null
    var superChapterId = 0
    var superChapterName: String? = null
    var title: String? = null
    var type = 0
    var userId = 0
    var visible = 0
    var zan = 0


    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val article = o as Article
        return id == article.id
    }

    override fun hashCode(): Int {
        return id
    }
}