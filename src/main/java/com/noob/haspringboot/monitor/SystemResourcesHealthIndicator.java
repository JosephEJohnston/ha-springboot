package com.noob.haspringboot.monitor;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import java.io.File;

@Component
public class SystemResourcesHealthIndicator implements HealthIndicator {

    private static final long MIN_FREE_MEMORY_BYTES = 100 * 1024 * 1024; // 100MB
    private static final long MIN_FREE_DISK_BYTES = 500 * 1024 * 1024;   // 500MB

    @Override
    public Health health() {
        long freeMemory = Runtime.getRuntime().freeMemory();
        File root = new File(".");
        long freeDiskSpace = root.getFreeSpace();

        boolean memoryHealthy = freeMemory > MIN_FREE_MEMORY_BYTES;
        boolean diskHealthy = freeDiskSpace > MIN_FREE_DISK_BYTES;

        Health.Builder status = (memoryHealthy && diskHealthy) ? Health.up() : Health.down();

        return status
                .withDetail("memory_status", memoryHealthy ? "OK" : "LOW_MEMORY")
                .withDetail("disk_status", diskHealthy ? "OK" : "LOW_DISK_SPACE")
                .build();
    }
}
