package com.demo.drawland.mvp

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author: hujw
 * @time: 2022/12/5
 * @desc: Mvp 实例化注解
 */
@Target(AnnotationTarget.FIELD) // 字段注解
@Retention(RetentionPolicy.RUNTIME) // 运行时注解

annotation class MvpInject