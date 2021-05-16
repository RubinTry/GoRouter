package cn.rubintry.annotation.kotlin


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class AutoWired(val name: String)
