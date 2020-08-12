package ctrl.api

import ctrl.common.Auth
import ctrl.common.Author
import ctrl.common.BadRequestException
import ctrl.common.UnauthException
import data.UserEntity
import lib.log.log
import org.hibernate.SessionFactory
import org.hibernate.exception.ConstraintViolationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.math.BigInteger
import java.util.*
import javax.persistence.PersistenceException
import kotlin.collections.HashMap

@RestController
@RequestMapping("/api/user")
class UserManager : Author {
    /** 依赖注入：数据库会话工厂 */
    @Autowired private val factory: SessionFactory? = null
    /** 依赖注入：验证码管理器 */
    @Autowired private val verificationCodeManager: VerificationCodeManager? = null

    /** API: 检测用户名是否被注册过 */
    @GetMapping("check/{nickname}")
    private fun checkNickname(@PathVariable nickname: String): Any {
        log.d("检测用户名可用性", nickname)
        factory!!.openSession()!!.use { session ->
            val rs = session.createNativeQuery("select count(*) from user where binary nickname=:nickname")
                    .setParameter("nickname", nickname)
                    .uniqueResult()
            rs as BigInteger
            return mapOf("available" to (rs == BigInteger.ZERO))
        }
    }

    /** 用户注册时的请求体数据类 */
    private data class RegisterJSON(var nickname: String, var password: String,
                                    var email: String, var serialNumber: Int, var verificationCode: Int)

    /** API: 新用户注册 */
    @PostMapping("register")
    private fun register(@RequestBody json: RegisterJSON): Any {
        log.d("注册请求", json)
        if (!verificationCodeManager!!.checkCode(json.serialNumber, json.verificationCode, 120_000)) {
            throw BadRequestException("注册失败", "验证码不正确或过期，请稍后重试")
        }
        factory!!.openSession()!!.use { session ->
            return try {
                session.beginTransaction()
                val rs = session.createNativeQuery(
                        "insert into user(nickname, password, email) value(:nickname, md5(:password), :email)")
                        .setParameter("nickname", json.nickname)
                        .setParameter("password", json.password)
                        .setParameter("email", json.email)
                        .executeUpdate()
                session.transaction.commit()
                if (rs == 1) mapOf("message" to "ok") else throw RuntimeException("未知的错误")
            } catch (error: PersistenceException) {
                log.d("异常", error)
                if (error.cause is ConstraintViolationException) { // 违反约束，可能是邮箱或用户名已注册过
                    throw BadRequestException("注册失败", "邮箱或用户名已被注册")
                }
                throw error
            }
        }
    }

    /** API: 查询用户的公开数据 */
    @GetMapping("query/{uid}")
    private fun query(@PathVariable uid: Int): Any {
        log.d("查询用户", uid)
        factory!!.openSession()!!.use { session->
            return session.createNativeQuery("select * from user where uid=:uid", UserEntity::class.java)
                    .setParameter("uid", uid)
                    .setMaxResults(1)
                    .resultList
        }
    }

    /** API: 返回认证用户数据 */
    @PostMapping("self")
    private fun querySelf(@RequestBody json: Auth): Any {
        log.d("用户认证", json.uid)
        auth(json)
        factory!!.openSession()!!.use { session->
            return session.createNativeQuery("select * from user where uid=:uid", UserEntity::class.java)
                    .setParameter("uid", json.uid)
                    .setMaxResults(1)
                    .resultList
        }
    }

    /** 映射已登录用户 */
    private val loggedInUser = HashMap<Int, LinkedList<Auth>>() // 使用数组，以允许多端在线
    /** 用户登录时的请求体数据类 */
    private data class LoginJSON(var nickname: String?, var email: String?, var password: String)

    /**
     * API: 用户登录
     * 基于前端axios类库的会话跟踪技术
     */
    @PostMapping("login")
    private fun login(@RequestBody json: LoginJSON): Auth {
        factory!!.openSession()!!.use { session ->
            // 非空检测，必须提供用户名或者邮箱
            json.nickname?: json.email?: throw BadRequestException("拒绝登录", "请提供正确的登录数据")
            val rs = session.createNativeQuery(
                    "select md5(:testPassword), password, uid, md5(:key) from user " +
                    "where binary ${ if (json.nickname != null) "nickname" else "email" }=:value")
                    .setParameter("testPassword", json.password)
                    .setParameter("value", json.nickname?: json.email)
                    .setParameter("key", json.password + (Math.random() * 100).toInt())
                    .setMaxResults(1)
                    .resultList.let {
                        if (it.size == 0) throw BadRequestException("用户不存在", "用户尚未注册，请先注册")
                        (it  as List<Array<*>>)[0]
                    }
            if (rs[0] != rs[1]) // 密码不正确
                throw BadRequestException("密码错误", "登录失败，密码错误")
            val uid = rs[2] as Int
            val key = rs[3] as String
            return Auth(uid, key, System.currentTimeMillis()).apply {
                // 获取用户的已登录密钥，若无，则创建
                val auths = loggedInUser[uid]?: LinkedList<Auth>().apply {
                    loggedInUser[uid] = this
                }
                // 设置登录上限
                if (auths.size > 5) auths.clear()
                // 添加新生成的认证密钥
                auths.push(this)
                log.d("密码正确，用户${ json.nickname?: json.email }(${ uid })登录成功", this)
            }
        }
    }

    /** 用户登出时的请求体数据类 */
    private data class LogoutBody(val action: String, val auth: Auth)

    /** API: 用户登出 */
    @PostMapping("/logout")
    private fun logout(@RequestBody json: LogoutBody): Any {
        if (json.action != "logout") throw BadRequestException("未知操作", "action: ${json.action}")
        auth(json.auth)
        // 获取用户的已登录密钥
        val auths = loggedInUser[json.auth.uid]
        auths?.forEach {
            if (it.key == json.auth.key) {
                auths.remove(it)
                if (auths.size == 0) // 释放内存
                    loggedInUser.remove(json.auth.uid)
                return mapOf("message" to "ok")
            }
        }
        return mapOf("message" to "ok")
    }

    /**
     * 实现接口Author：用户身份认证
     * @return
     * * true 用户身份可信
     */
    override fun auth(auth: Auth): Boolean {
        // 获取用户的已登录密钥
        val auths = loggedInUser[auth.uid]
        // 检查时间
        val currentTime = System.currentTimeMillis()
        auths?.forEach {
            if (currentTime - it.loginTime > 30 * 24 * 60 * 60 * 1000L) // 超过30天，登录失效
                auths.remove(it)
            else if (it.key == auth.key)
                return true
        }
        throw UnauthException()
    }
}
