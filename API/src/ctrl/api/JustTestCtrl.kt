package ctrl.api

import ctrl.common.Auth
import ctrl.common.Author
import ctrl.common.BadRequestException
import lib.log.log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/test")
class JustTestCtrl {
    @Autowired val servletContext: ApplicationContext? = null
    @Autowired val author: Author? = null
    @GetMapping("null") fun returnNull() = null
    @GetMapping("badreq") fun badreq(): Nothing = throw BadRequestException("错误")
    @PostMapping("auth") fun auth(@RequestBody auth: Auth) = author!!.auth(auth)
    // 测试文件上传
    @PostMapping("upload")
    fun upload(@RequestPart file: Array<MultipartFile>) {
//        log.d("文件保存至", ctx.catalinaBase)
        log.d(file.size)
        file.forEach {
            log.d(it.name)
            log.d(it.contentType)
            log.d(it.size)
        }
    }
}
