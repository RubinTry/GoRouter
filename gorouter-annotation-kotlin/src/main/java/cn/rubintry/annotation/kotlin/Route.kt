package cn.rubintry.annotation.kotlin


@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Route(val path: String)
