package ctrl.advice

import ctrl.common.BadRequestException
import ctrl.common.UnauthException
import lib.log.log
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
@ResponseBody
private class DefaultAdvice {

	/**
	 * 对于服务器错误，message对应Throwable的消息
	 *
	 * 对于控制器抛出的客户端错误，控制器必须提供一个error类型，message可选
	 */
	private data class CustomError(var error: String, var message: String? = null)

	/**
	 * 服务器错误，发送500响应
	 */
	@ExceptionHandler(Throwable::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
	private fun serverError(error: Throwable): CustomError {
		log.d("系统错误", error)
		return CustomError("系统错误", error.message)
	}

	/**
	 * 客户端错误，发送400响应
	 */
	@ExceptionHandler(BadRequestException::class)
	@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    private fun clientError(error: BadRequestException): CustomError {
		return CustomError(error.error, error.message)
	}

	/**
	 * 用户身份认证错误，发送401响应
	 */
	@ExceptionHandler(UnauthException::class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
	private fun unauthenticatedError(error: UnauthException): CustomError {
		return CustomError(error.message!!)
	}
}