package server

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import lib.log.log
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.multipart.MultipartResolver
import org.springframework.web.multipart.support.StandardServletMultipartResolver
import org.springframework.web.servlet.config.annotation.*

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = ["ctrl"])
@Import(HibernateConfig::class) // 导入Hibernate配置类
open class WebConfig : WebMvcConfigurer {
    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        // 设置Content-Type默认值
        configurer.defaultContentType(MediaType.APPLICATION_JSON)
    }

    override fun configureDefaultServletHandling(configurer: DefaultServletHandlerConfigurer) {
        log.d("Spring MVC就绪")
//        configurer.enable() // MVC未映射的请求交由容器提供的默认Servlet来处理
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        // 字符串消息转换器
        converters.add(StringHttpMessageConverter(Charsets.UTF_8).apply {
            setWriteAcceptCharset(false) // 不发送可响应字符串的编码
        })

        // Jackson消息转换器
        converters.add(MappingJackson2HttpMessageConverter().apply {
            // 是否格式化缩进
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
            // 反序列化时遇见未知参数是否失败
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            log.d("Jackson消息转换器就绪", supportedMediaTypes)
        })
    }

    @Bean
    open fun initMultipartResolver(): MultipartResolver {
        return StandardServletMultipartResolver()
    }
}