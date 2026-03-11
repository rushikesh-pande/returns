package com.order.returns.db.search;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Database Optimisation Enhancement: Elasticsearch Search Repository
 * Provides full-text search over returns entities.
 */
@Repository
public interface ReturnSearchRepository
        extends ElasticsearchRepository<ReturnSearchDocument, String> {

    List<ReturnSearchDocument> findByNameContainingOrDescriptionContaining(
            String name, String description);

    List<ReturnSearchDocument> findByStatus(String status);

    List<ReturnSearchDocument> findByCategory(String category);

    @Query("{\"multi_match\":{\"query\":\"?0\",\"fields\":[\"name^2\",\"description\",\"category\"],\"fuzziness\":\"AUTO\",\"type\":\"best_fields\"}}")
    List<ReturnSearchDocument> fuzzySearch(String query);
}
