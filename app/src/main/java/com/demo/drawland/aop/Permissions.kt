package com.demo.drawland.aop

/**
 * author :hujw
 * time : 2022/11/14
 * desc : 权限申请注解
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER)
annotation class Permissions constructor(
    /**
     * 需要申请权限的集合
     */
    vararg val value: String
)