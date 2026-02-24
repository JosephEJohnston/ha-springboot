package com.noob.haspringboot.controller;

import com.noob.haspringboot.common.Result;
import com.noob.haspringboot.service.TradeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chaos")
public class ChaosController {

    @Resource
    private TradeService tradeService;

    @GetMapping("/memory-leak")
    public Result<String> leak() {
        // 模拟内存快速膨胀
        List<byte[]> list = new ArrayList<>();
        // 每次请求分配 10MB，直到 OOM 或触发健康检查 DOWN
        for (int i = 0; i < 50; i++) {
            list.add(new byte[10 * 1024 * 1024]);
        }
        return Result.success("Memory consumed: " + list.size() * 10 + "MB");
    }

    @GetMapping("/long-task")
    public Result<String> slowTask(@RequestParam int seconds) throws InterruptedException {
        log.info("开始执行耗时任务，预计 {} 秒", seconds);
        Thread.sleep(seconds * 1000L); // 模拟耗时操作
        return Result.success("Task finished after " + seconds + "s");
    }

    @GetMapping("/async-flood")
    public Result<String> flood() {
        // 触发异步任务，填满你那个 1000 容量的 queueCapacity
        for (int i = 0; i < 100; i++) {
            tradeService.processAsyncOrder(System.currentTimeMillis());
        }
        return Result.success("Flood triggered");
    }

    @GetMapping("/cpu-burn")
    public Result<Long> burn() {
        long startTime = System.currentTimeMillis();
        // 简单的死循环计算，模拟 CPU 100%
        while (System.currentTimeMillis() - startTime < 5000) {
            Math.sqrt(Math.random());
        }
        return Result.success(System.currentTimeMillis());
    }
}
