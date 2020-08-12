package ctrl.common

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * 身份验证数据类
 */
data class Auth(var uid: Int, var key: String, @JsonIgnore var loginTime: Long)
