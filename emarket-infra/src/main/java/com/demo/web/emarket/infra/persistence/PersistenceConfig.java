package com.demo.web.emarket.infra.persistence;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.demo.web.emarket")
@EntityScan(
        basePackages = {"com.demo.web.emarket", "org.springframework.data.jpa.convert.threeten"}
)
@ComponentScan(
        basePackages = {"com.demo.web.emarket"}
)
@EnableAutoConfiguration
public class PersistenceConfig {
}
