package ctrl.api

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * 处理404请求
 */
@RestController
private class DefaultCtrl {
    @RequestMapping("/**")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private fun notFound(): String = """{"error":"404","message":"错误的API请求"}"""
}