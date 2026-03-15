package com.noob.haspringboot.controller;

import com.noob.haspringboot.model.Stock;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Controller
public class MarketController {

    private final Random random = new Random();

    private List<Stock> getMockMarketData() {
        return List.of(
                new Stock("510300", "沪深300ETF", String.format("%.3f", 3.500 + random.nextDouble() * 0.1), random.nextBoolean()),
                new Stock("512890", "红利ETF", String.format("%.3f", 2.800 + random.nextDouble() * 0.1), random.nextBoolean()),
                new Stock("513050", "中概互联ETF", String.format("%.3f", 1.200 + random.nextDouble() * 0.1), random.nextBoolean())
        );
    }

    @GetMapping("/market")
    public String marketDashboard(Model model, @RequestHeader(value = "HX-Request", required = false) boolean isHtmxRequest) {
        model.addAttribute("stocks", getMockMarketData());
        model.addAttribute("updateTime", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // JTE 改造核心：
        // HTMX 请求时，我们直接渲染那个独立的 tag 文件
        if (isHtmxRequest) {
            return "tag/stockTable"; // 对应 src/main/jte/tag/stockTable.jte
        }

        return "market";
    }
}