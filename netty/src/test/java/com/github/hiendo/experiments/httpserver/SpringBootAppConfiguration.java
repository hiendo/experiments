package com.github.hiendo.experiments.httpserver;

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
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


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
        JettyEmbeddedServletContainerFactory embeddedServletContainerFactory =
                new JettyEmbeddedServletContainerFactory(8888);

        return embeddedServletContainerFactory;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(SpringBootAppConfiguration.class);
        app.run(args);
    }
}
