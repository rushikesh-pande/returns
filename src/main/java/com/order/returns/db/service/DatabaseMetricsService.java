package com.order.returns.db.service;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Database Optimisation Enhancement: Database Metrics Service
 *
 * Tracks cache and query performance metrics for returns.
 * Exposed to Prometheus via /actuator/prometheus.
 *
 * Metrics:
 *  - returns_cache_hits_total       — Redis cache hits
 *  - returns_cache_misses_total     — Redis cache misses (DB queries)
 *  - returns_db_queries_total       — Total DB queries by type
 *  - returns_db_slow_queries_total  — Queries above 500ms
 *  - returns_connection_pool_active — HikariCP active connections
 */
@Service
public class DatabaseMetricsService {

    private final MeterRegistry meterRegistry;
    private final AtomicLong activeConnections = new AtomicLong(0);

    public DatabaseMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        Gauge.builder("returns.connection.pool.active", activeConnections, AtomicLong::get)
             .description("Active HikariCP connections for returns")
             .tag("service", "returns")
             .register(meterRegistry);
    }

    public void recordCacheHit(String cacheName) {
        Counter.builder("returns.cache.hits.total")
               .tag("service", "returns").tag("cache", cacheName)
               .description("Redis cache hits for returns")
               .register(meterRegistry).increment();
    }

    public void recordCacheMiss(String cacheName) {
        Counter.builder("returns.cache.misses.total")
               .tag("service", "returns").tag("cache", cacheName)
               .description("Redis cache misses for returns (DB fallback)")
               .register(meterRegistry).increment();
    }

    public void recordDbQuery(String queryType) {
        Counter.builder("returns.db.queries.total")
               .tag("service", "returns").tag("type", queryType)
               .description("DB queries for returns")
               .register(meterRegistry).increment();
    }

    public void recordSlowQuery(String queryType, long ms) {
        Counter.builder("returns.db.slow.queries.total")
               .tag("service", "returns").tag("type", queryType)
               .description("DB queries exceeding 500ms for returns")
               .register(meterRegistry).increment();
        meterRegistry.summary("returns.db.query.duration",
                "service", "returns", "type", queryType).record(ms);
    }

    public void setActiveConnections(long count) {
        activeConnections.set(count);
    }
}
