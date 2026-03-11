package com.order.returns.db.health;

import org.springframework.boot.actuate.health.*;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

/**
 * Database Optimisation Enhancement: Elasticsearch Health Indicator
 */
@Component("elasticsearchHealth")
public class ElasticsearchHealthIndicator implements HealthIndicator {

    private final ElasticsearchTemplate elasticsearchTemplate;

    public ElasticsearchHealthIndicator(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Health health() {
        try {
            boolean up = elasticsearchTemplate.indexOps(
                    org.springframework.data.elasticsearch.core.IndexCoordinates.of("returns-index"))
                    .exists();
            return Health.up()
                    .withDetail("service",       "returns")
                    .withDetail("elasticsearch", "reachable")
                    .withDetail("index",         "returns-index (exists=" + up + ")")
                    .build();
        } catch (Exception ex) {
            return Health.down()
                    .withDetail("service",       "returns")
                    .withDetail("elasticsearch", "unreachable")
                    .withDetail("error",         ex.getMessage())
                    .build();
        }
    }
}
