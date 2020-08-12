import emcat.MyCat
import lib.log.log
import server.WebAppInitializer

fun main() {
    MyCat("0.0.0.0", 8080).apply {
        try {
            spring(WebAppInitializer())
            service()
        } catch (e: Throwable) {
            e.printStackTrace()
            log.d("服务器启动失败")
        }
    }
}