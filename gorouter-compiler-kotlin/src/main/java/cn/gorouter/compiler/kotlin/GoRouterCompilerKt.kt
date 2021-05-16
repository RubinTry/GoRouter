package cn.gorouter.compiler.kotlin

import cn.rubintry.annotation.kotlin.Route
import com.google.auto.service.AutoService
import java.io.IOException
import java.io.Writer
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.JavaFileObject

@AutoService(Processor::class) //Register a annotation processor
//Register a annotation processor
class GoRouterCompilerKt :  BaseProcessor(){

    private var filer : Filer ?= null

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
        types.add(Route::class.java.canonicalName)
        return types
    }



    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        val elementsAnnotatedWith: Set<Element?> = roundEnv?.getElementsAnnotatedWith(Route::class.java) as Set<Element?>
        // TypeElement  类节点 代表一个类
        //ExecutableElement 方法节点
        // VariableElement成员变量节点
        val map: MutableMap<String, String> = HashMap()
        for (element in elementsAnnotatedWith) {
            val typeElement = element as TypeElement
            val url: String = typeElement.getAnnotation(Route::class.java).path
            val nodeName = typeElement.qualifiedName.toString()
            map[url] = "$nodeName.class"
        }
        processRoute(map)
        return false
    }

    override fun processRoute(nodeMap: MutableMap<String , String>) {


        if(nodeMap.isNotEmpty()){
            writeToCache(nodeMap)
        }
    }

    override fun writeToCache(map: MutableMap<String, String>) {
        var writer: Writer? = null
        //Create a new className.
        //Create a new className.
        val nodeName = "GoRouter_node_" + System.currentTimeMillis() + "_Impl"
        //Generate a file.
        //Generate a file.
        try {
            val sourceFile: JavaFileObject? = filer?.createSourceFile("cn.gorouter.route.$nodeName")
            writer = sourceFile?.openWriter()
            writer?.write("""package cn.gorouter.route;

import cn.gorouter.api.kotlin.core.Core;
import cn.gorouter.api.kotlin.core.IRouter;

/**
 * @author logcat
 */
public class $nodeName implements IRouter {
    @Override
    public void put() {
""")
            val iterator: Iterator<String> = map.keys.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                val nodeNames = map[key]
                writer?.write("""
        Core.getInstance().put("$key" , $nodeNames);""")
            }
            writer?.write("""
    }
}""")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                writer?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}