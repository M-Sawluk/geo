package com.michal.geo_points.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.michal.geo_points.model.GeoDocument;

public interface GeoPointsRepo extends ElasticsearchRepository<GeoDocument, Long> {

}
