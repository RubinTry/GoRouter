package cn.gorouter.compiler.kotlin

import cn.rubintry.annotation.kotlin.AutoWired
import com.google.auto.service.AutoService
import java.io.IOException
import java.io.Writer
import java.util.*
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.JavaFileObject


@AutoService(Processor::class) //Register a annotation processor
class AutoWiredCompilerKt : BaseProcessor() {


    private var filer: Filer? = null

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment?) {
        super.init(processingEnvironment)
        filer = processingEnvironment?.filer
    }


    /**
     * Declare the Java version supported by the annotation processor
     *
     * @return
     */
    override fun getSupportedSourceVersion(): SourceVersion? {
        return processingEnv.sourceVersion
    }


    /**
     * What are the annotations to be processed by the annotation processor
     *
     * @return
     */
    override fun getSupportedAnnotationTypes(): Set<String>? {
        val types: MutableSet<String> = HashSet()
        types.add(AutoWired::class.java.canonicalName)
        return types
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
//        val elementsAnnotationWith = roundEnv?.getElementsAnnotatedWith(AutoWired::class.java) as Set<Element?>
//        val map : MutableMap<String , String> = mutableMapOf()
//        for (element in elementsAnnotationWith) {
//            val routePath = element?.getAnnotation(AutoWired::class.java)?.name ?: ""
//            val targetType = element?.enclosingElement.toString()
//            map[routePath] = targetType
//            print("routePath:$routePath")
//        }
//        processRoute(map)
//        return false

        if (annotations.isNullOrEmpty().not()) {
            logger?.info(">>> Found autowired field, start... <<<")

            return true
        }

        return false
    }


    /**
     * 初始化路由节点
     *
     * @param nodeMap
     */
    override fun processRoute(nodeMap: MutableMap<String, String>) {
        if (nodeMap.isNotEmpty()) {
            writeToCache(nodeMap)
        }
    }

    override fun writeToCache(nodeMap: MutableMap<String, String>) {
        var writer: Writer? = null
        val clazzName = "AutoWire_${System.currentTimeMillis()}_Impl"

        try {
            val sourceFile: JavaFileObject? = filer?.createSourceFile("cn.gorouter.route.$clazzName")
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            try {
                writer?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}