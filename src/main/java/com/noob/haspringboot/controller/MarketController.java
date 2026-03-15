package com.noob.haspringboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
public class MarketController {

    private final Random random = new Random();

    // 模拟获取自选股行情数据
    private List<Map<String, Object>> getMockMarketData() {
        return Arrays.asList(
                Map.of("code", "510300", "name", "沪深300ETF", "price", String.format("%.3f", 3.500 + random.nextDouble() * 0.1), "isUp", random.nextBoolean()),
                Map.of("code", "512890", "name", "红利ETF", "price", String.format("%.3f", 2.800 + random.nextDouble() * 0.1), "isUp", random.nextBoolean()),
                Map.of("code", "513050", "name", "中概互联ETF", "price", String.format("%.3f", 1.200 + random.nextDouble() * 0.1), "isUp", random.nextBoolean())
        );
    }

    @GetMapping("/market")
    public String marketDashboard(Model model, @RequestHeader(value = "HX-Request", required = false) boolean isHtmxRequest) {
        model.addAttribute("stocks", getMockMarketData());
        model.addAttribute("updateTime", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // 【核心魔法】
        // 如果请求头里带有 HX-Request，说明这是 HTMX 发起的局部刷新
        // 我们只返回模板里的 "stock-table" 那个局部片段 (Fragment)
        if (isHtmxRequest) {
            return "market :: stock-table";
        }

        // 否则（比如浏览器刚打开），返回完整的整张页面
        return "market";
    }
}