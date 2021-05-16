package cn.gorouter.api.kotlin.core

object WareHouse {
    var routeMap: MutableMap<String, Class<*>> = mutableMapOf()
    var panelMap: MutableMap<String, Panel> = mutableMapOf()
}