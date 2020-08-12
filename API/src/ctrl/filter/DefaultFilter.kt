package ctrl.filter

import lib.log.log
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class DefaultFilter : HttpFilter() {
    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        with (request) {
            log.d("新的HTTP请求")
            log.d(method, if (queryString == null) requestURI else "${ requestURI }?${ queryString }")
            headerNames.iterator().forEach {
                log.d(it, ":", getHeader(it))
            }
        }
        response.addHeader("Server", "MyCat")
        response.addHeader("Access-Control-Allow-Origin", "*")
        response.addHeader("Access-Control-Allow-Methods", "*")
        response.addHeader("Access-Control-Allow-Headers", "*")
//        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
//        response.addHeader("Access-Control-Allow-Headers",
//                "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With")
        arrayOf("OPTIONS", "options").forEach {
            if (request.method == it) {
                response.status = 204;
                return
            }
        }
        chain.doFilter(request, response)
    }
}
