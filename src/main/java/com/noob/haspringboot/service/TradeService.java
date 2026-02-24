package com.noob.haspringboot.service;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService {

    private final ObservationRegistry observationRegistry;

    /**
     * 执行异步订单处理
     * <p>确保使用我们配置的、带上下文传播的线程池</p>
     */
    @Async("threadPoolTaskExecutor")
    public void processAsyncOrder(Long orderId) {
        // 1. 此时日志会自动带上和主线程一致的 [TraceId, SpanId]
        log.info(">>> 异步线程开始处理订单: {}", orderId);

        // 2. 使用 Observation 手动埋点，这样 Zipkin 里能看到这段业务的详细耗时
        Observation.createNotStarted("trade.order.process", observationRegistry)
                .contextualName("processing-order-" + orderId)
                .lowCardinalityKeyValue("order.id", String.valueOf(orderId))
                .observe(() -> {
                    try {
                        // 模拟硬核的交易逻辑计算（比如你研究的 Al Brooks 价格行为分析）
                        doHardWork();
                        log.info("订单 {} 逻辑计算完成", orderId);
                    } catch (Exception e) {
                        log.error("订单处理发生异常", e);
                    }
                });

        log.info("<<< 异步线程处理结束: {}", orderId);
    }

    private void doHardWork() throws InterruptedException {
        // 模拟随机耗时，方便你在 Zipkin 看到长短不一的彩条
        long sleepTime = ThreadLocalRandom.current().nextLong(500, 2000);
        Thread.sleep(sleepTime);
    }
}