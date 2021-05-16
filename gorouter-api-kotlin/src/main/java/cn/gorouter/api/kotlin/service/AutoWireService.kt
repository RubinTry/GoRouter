package cn.gorouter.api.kotlin.service

import cn.gorouter.api.kotlin.pub.IProvider

interface AutoWireService : IProvider {

    fun autoWire(instance: Any)
}