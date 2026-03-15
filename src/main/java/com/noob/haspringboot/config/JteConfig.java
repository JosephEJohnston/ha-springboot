package com.noob.haspringboot.config;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.springframework.boot.autoconfigure.JteProperties;
import gg.jte.springframework.boot.autoconfigure.JteViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class JteConfig {

    @Bean
    public JteViewResolver jteViewResolver(TemplateEngine templateEngine, JteProperties jteProperties) {
        // 传入两个参数：引擎和配置属性
        JteViewResolver resolver = new JteViewResolver(templateEngine, jteProperties);

        // 设置为最高优先级，彻底干掉 Circular view path
        resolver.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return resolver;
    }

    @Bean
    public TemplateEngine templateEngine() {
        // 在 Native 模式下，必须使用并初始化 precompiled 模式
        return TemplateEngine.createPrecompiled(ContentType.Html);
    }
}


