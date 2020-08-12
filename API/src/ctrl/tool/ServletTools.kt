package ctrl.tool

import javax.servlet.http.HttpServletRequest

// 获取客户端真实IP地址，考虑反向代理的情况
fun getRemoteAddr(req: HttpServletRequest): String {
    return req.remoteAddr
}
