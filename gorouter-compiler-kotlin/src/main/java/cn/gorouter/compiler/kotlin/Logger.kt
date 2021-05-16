package cn.gorouter.compiler.kotlin

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

class Logger(messager: Messager) {

    private val TAG = "GoRouter::Compiler "
    private var msg: Messager? = null

    init {
        msg = messager
    }

    /**
     * Print info log.
     */
    fun info(info: CharSequence) {
        if (info.isNotEmpty()) {
            msg?.printMessage(Diagnostic.Kind.NOTE, TAG + info)
        }
    }

    fun error(error: CharSequence) {
        if (error.isNotEmpty()) {
            msg?.printMessage(Diagnostic.Kind.ERROR, "${TAG}An exception is encountered, [" + error + "]")
        }
    }

    fun error(error: Throwable?) {
        if (null != error) {
            msg?.printMessage(Diagnostic.Kind.ERROR, "${TAG}An exception is encountered, [" + error.message + "]" + "\n" + formatStackTrace(error.stackTrace))
        }
    }

    fun warning(warning: CharSequence) {
        if (warning.isNotEmpty()) {
            msg?.printMessage(Diagnostic.Kind.WARNING, "${TAG}${warning}")
        }
    }

    private fun formatStackTrace(stackTrace: Array<StackTraceElement>): String {
        val sb = StringBuilder()
        for (element in stackTrace) {
            sb.append("    at ").append(element.toString())
            sb.append("\n")
        }
        return sb.toString()
    }
}