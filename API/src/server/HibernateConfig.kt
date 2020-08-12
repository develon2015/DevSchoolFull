package server

import lib.config.JsonConfig
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.hibernate5.HibernateTransactionManager
import org.springframework.orm.hibernate5.LocalSessionFactoryBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import javax.sql.DataSource

@Configuration
@ActiveProfiles("dev")
@EnableTransactionManagement
open class HibernateConfig {
    /**
     * 在Spring中，我们要通过Spring的某个SessionSessionFactoryBean来获取Hibernate SessionFactory
     * Spring3.1之后，对于Hibernate4.0+，LocalSessionFactory结合了AnnotationSessionFactory，在XML基础上实现了注解开发支持
     * 现在可以在控制器中自动填充SessionFactory依赖
     */
    @Bean
    @Autowired
    open fun initSessionFactory(dataSource: DataSource): LocalSessionFactoryBean {
        return LocalSessionFactoryBean().apply {
            // 设置数据源
            setDataSource(dataSource)
            // 设置需要扫描的数据库实体类所在的包，这些类通过注解的方式表明要使用Hibernate进行持久化
            // 这些类可以使用的注解包括JPA的@Entity或@MappedSuperclass以及Hibernate的@Entity。
            setPackagesToScan("data")
            // 设置Hibernate参数
            hibernateProperties = Properties().apply {
                setProperty("hibernate.show_sql", "true") // 将hibernate生成的sql语句打印到控制台
                setProperty("hibernate.format_sql", "false") // 将hibernate生成的sql语句格式化(语法缩进)
                setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect") // 数据库方言
//                setProperty("hibernate.hbm2ddl.auto", "update") // 自动导出表结构. 自动建表
            }
        }
    }

    /**
     * 如果不能从JNDI中查找数据源,那么最好的选择就是直接在Spring中配置数据源连接池。
     * 尽管Spring并没有提供数据源连接池实现,但是我们有多项可用的方案,包括开源的Tomcat DBCP
     */
    @Bean
    open fun initDataSource(): DataSource {
        return BasicDataSource().apply {
            with (JsonConfig("./assets/dbconfig.json")) {
                driverClassName = get("db_driver")
                username = get("db_user")
                password = get("db_password")
                url = get("db_url")
            }
        }
    }

    /**
     * HibernateTransactionManager实例负责数据库事务处理
     */
    @Bean
    @Autowired
    open fun initTransactionManager(sessionFactory: SessionFactory): HibernateTransactionManager {
        return HibernateTransactionManager().apply {
            this.sessionFactory = sessionFactory
        }
    }
}