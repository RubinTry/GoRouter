package cn.gorouter.compiler.kotlin

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

abstract class BaseProcessor : AbstractProcessor() {
    protected var logger : Logger ?= null

    override fun init(processingEnvironment: ProcessingEnvironment?) {
        super.init(processingEnvironment)
        processingEnvironment?.messager?.let {
            logger = Logger(it)
        }

    }
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {

        return false
    }

    protected abstract fun processRoute(nodeMap: MutableMap<String , String>)

    protected abstract fun writeToCache(nodeMap: MutableMap<String , String>)
}