package ctrl.common

import java.lang.RuntimeException

class UnauthException() : RuntimeException("未认证用户")