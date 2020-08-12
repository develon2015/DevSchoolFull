package ctrl.tool

import lib.log.log

// 发送邮件
fun sendEmail(to: String, title: String, content: String) {
    log.d("邮件发送至", to)
    log.d("标题", title)
    log.d("内容", content)
    // TODO: 对接邮件接口
}