package cn.gorouter.api.exception

import java.lang.RuntimeException

class RouteNotFoundException(message: String?) : RuntimeException(message) {
}