package ctrl.common

import java.lang.RuntimeException

class BadRequestException(val error: String, message: String? = null) : RuntimeException(message)