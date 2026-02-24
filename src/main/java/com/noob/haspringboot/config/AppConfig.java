package com.noob.haspringboot.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.DefaultTracingObservationHandler;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@EnableAspectJAutoProxy
@Configuration
public class AppConfig {

    /**
     * 核心：支撑 @Observed 注解
     * 让你能在 Service 层直接用注解记录“行车数据”
     */
    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

    // 2. 强制将 Observation 和 Tracer 绑定
    @Bean
    void registerTracingHandler(ObservationRegistry observationRegistry, Tracer tracer) {
        observationRegistry.observationConfig()
                .observationHandler(new DefaultTracingObservationHandler(tracer));
    }

    /**
     * 核心：上下文传播装饰器
     * 它负责在任务提交前“截图”当前的 TraceId、MDC、SecurityContext
     * 并在异步线程启动时自动“还原”
     */
    @Bean
    public TaskDecorator contextPropagatingTaskDecorator() {
        return new ContextPropagatingTaskDecorator();
    }

    /**
     * 配置你的“自愈型”线程池
     */
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        return builder
                .corePoolSize(10)
                .maxPoolSize(50)
                .queueCapacity(1000)
                .threadNamePrefix("ha-async-")
                // 关键点：将装饰器挂载到线程池上
                .taskDecorator(contextPropagatingTaskDecorator())
                .build();
    }
}
