package ctrl.tool

import java.net.URLEncoder

fun stringify(obj: Any?) = URLEncoder.encode(obj.toString(), Charsets.UTF_8.name())