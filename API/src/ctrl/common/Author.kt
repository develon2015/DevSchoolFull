package ctrl.common

interface Author {
    /**
     * 用户身份认证
     * @return
     * * true 用户身份可信
     * @throws UnauthException 用户未认证
     */
    fun auth(auth: Auth): Boolean
}