package ctrl.listener

import lib.log.log
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEvent
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import kotlin.concurrent.thread

@Service
private class EventListener {
	@Autowired private var appContext: ApplicationContext? = null
	@Autowired private var factory: SessionFactory? = null

	@EventListener
	fun onEvent(event: ApplicationEvent) {
		log.d("Spring事件", event)
	}

	/** Spring启动完毕，数据库连接成功 */
	@EventListener
	fun onRefreshed(event: ContextRefreshedEvent) {
		"查询消息转换器".apply {
			log.d(this)
			val requestMappingHandlerAdapter = appContext?.getBean(RequestMappingHandlerAdapter::class.java)
			val messageConverters = requestMappingHandlerAdapter?.messageConverters
			log.d("Spring 共加载了 ${ messageConverters?.size } 个消息转换器对象:")
			messageConverters?.forEach {
				log.d(it::class.java.canonicalName)
			}
		}
		"启动数据库激活线程".apply {
            thread {
				log.d(this)
				//
			}
		}
	}
}