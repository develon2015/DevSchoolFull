package server

import ctrl.filter.DefaultFilter
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer
import javax.servlet.Filter
import javax.servlet.MultipartConfigElement
import javax.servlet.ServletRegistration

class WebAppInitializer : AbstractAnnotationConfigDispatcherServletInitializer() {
    override fun getRootConfigClasses(): Array<Class<*>>? {
        return null
    }

    override fun getServletMappings(): Array<String> {
        return arrayOf("/")
    }

    override fun getServletConfigClasses(): Array<Class<*>>? {
        return arrayOf(WebConfig::class.java)
    }

    override fun getServletFilters(): Array<Filter>? {
        return arrayOf(DefaultFilter())
    }

    override fun customizeRegistration(registration: ServletRegistration.Dynamic) {
        registration.setMultipartConfig(
            MultipartConfigElement("/tmp/",
                    100 * 1024 * 1024,
                    800 * 1024 * 1024,
                    100 * 1024)
        )
    }
}
