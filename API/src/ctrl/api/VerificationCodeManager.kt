package ctrl.api

import com.fasterxml.jackson.annotation.JsonIgnore
import ctrl.common.BadRequestException
import ctrl.tool.getRemoteAddr
import ctrl.tool.sendEmail
import lib.log.log
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * 验证码相关 API 的控制器
 * 将随机验证码发送到指定邮箱，以检测用户邮箱可用性及其归属
 * 用户请求验证码后，获得一个序列号serialNumber，验证码verificationCode则发送到指定邮箱
 * 相关控制器通过本实例的方法checkCode(serialNumber: Int, verificationCode: Int, time: Int): Boolean检验验证码的有效性
 */
@RestController
@RequestMapping("/api/verification_code")
class VerificationCodeManager {
    /** 依赖注入：数据库会话工厂 */
    @Autowired var factory: SessionFactory? = null
    /** 下一个生成的验证码序列号，递增，需要同步 */
    private var serial: Int = 1000
        @Synchronized set
        @Synchronized get

    /** 映射序列号和验证码 */
    private val codes = HashMap<Int, VerificationCode>()
    /** 映射请求验证码的客户端IP及最后请求时间 */
    private val ips = HashMap<String, Long>()

    /** 验证码数据类 */
    private data class VerificationCode(val serialNumber: Int, @JsonIgnore val verificationCode: Int, @JsonIgnore val requestTime: Long)
    /** 用户请求注册验证码时的请求体数据类 */
    private data class UserRegisterRequestJSON(val email: String)

    /** API: 处理新用户注册时的验证码请求 */
    @PostMapping("user_register")
    private fun userRegisterRequest(req: HttpServletRequest, @RequestBody json: UserRegisterRequestJSON): VerificationCode {
        if (System.currentTimeMillis() - (ips[getRemoteAddr(req)]?:0) < 10_000) {
            throw BadRequestException("请求过快", "请60秒后再试")
        }
        // 使用正则表达式检测邮箱是否合法
        if (!json.email.matches("""^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+${'$'}""".toRegex())) {
            throw BadRequestException("拒绝发送", "邮箱不正确")
        }
        // TODO: 检测邮箱是否已注册过账户
        factory!!.openSession()!!.use { session ->
            // 判断邮箱是否已注册，查询用户名
            val rs = session.createNativeQuery("select nickname from user where binary email=:email")
                    .setParameter("email", json.email)
                    .setMaxResults(1)
                    .uniqueResult()
            if (rs != null) {
                // 邮箱已注册，返回用户名，提示用户找回密码
                throw BadRequestException("邮箱已注册", rs as String)
            }
        }
        // 记录请求时间
        ips[getRemoteAddr(req)] = System.currentTimeMillis()
        return newCode().apply {
            // 发送邮件
            sendEmail(json.email, "注册验证码", """
                感谢注册DevSchool，您的验证码是：${ verificationCode }
            """.trimIndent())
        }
    }

    /** 生成验证码，并put到表codes中 */
    private fun newCode() = VerificationCode(++serial, code(), System.currentTimeMillis()).apply {
        log.d("验证码请求", this)
        codes[serial] = this
    }

    /** 返回一个6位随机数 */
    private fun code() = (100000 + Math.random() * 900000).toInt()

    /**
     * 检查验证码的正确性及时效
     * @param serialNumber 序列号
     * @param verificationCode 验证码
     * @param time 验证码有效期
     * @return
     * * true   验证通过
     * * false  验证码不正确或者过期
     */
    fun checkCode(serialNumber: Int, verificationCode: Int, time: Int): Boolean {
//        return true // 暂时禁用验证
        codes[serialNumber]?.let {
            if (it.verificationCode == verificationCode && System.currentTimeMillis() - it.requestTime < time) {
                codes.remove(serialNumber)
                return true
            }
        }
        return false
    }
}