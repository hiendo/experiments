package experiments.github.hiendo.experiments.httpserver;

import org.apache.catalina.Context;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.MongoTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.mobile.DeviceResolverAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.reactor.ReactorAutoConfiguration;
import org.springframework.boot.autoconfigure.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import java.util.Set;


@Configuration
@ComponentScan(basePackages = "com.github.hiendo.experiments.httpserver")
@EnableAutoConfiguration(
        exclude = {RabbitAutoConfiguration.class, AopAutoConfiguration.class, BatchAutoConfiguration.class,
                JpaRepositoriesAutoConfiguration.class, MongoAutoConfiguration.class,
                MongoTemplateAutoConfiguration.class, DataSourceAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class, JmsTemplateAutoConfiguration.class,
                JmxAutoConfiguration.class, DeviceResolverAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
                ReactorAutoConfiguration.class, RedisAutoConfiguration.class, SecurityAutoConfiguration.class,
                ThymeleafAutoConfiguration.class, EmbeddedServletContainerAutoConfiguration.EmbeddedTomcat.class,
                EmbeddedServletContainerAutoConfiguration.EmbeddedJetty.class, MultipartAutoConfiguration.class,
                WebSocketAutoConfiguration.class})
public class SpringBootAppConfiguration {
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(SpringBootAppConfiguration.class);

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory embeddedServletContainerFactory =
                new TomcatEmbeddedServletContainerFactory(8888);

        embeddedServletContainerFactory.addContextCustomizers(new TomcatContextCustomizer() {
            @Override
            public void customize(Context context) {
                context.setJarScanner(new JarScanner() {
                    @Override
                    public void scan(ServletContext context, ClassLoader classloader, JarScannerCallback callback,
                            Set<String> jarsToSkip) {
                        // Don't do any tomcat's jar scanning to make startup a little bit faster
                    }
                });
            }
        });

        return embeddedServletContainerFactory;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(SpringBootAppConfiguration.class);
        app.run(args);
    }
}
