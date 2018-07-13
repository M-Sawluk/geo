package com.michal.geo_points;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import com.michal.geo_points.model.GeoDocument;
import com.michal.geo_points.repository.GeoPointsRepo;
import com.michal.geo_points.service.Generator;

@SpringBootApplication
public class GeoPointsApplication {
	@Autowired
	private ElasticsearchTemplate esTemplate;
	@Autowired
	private GeoPointsRepo geoPointsRepo;
	@Autowired
	private Generator generator;

	public static void main(String[] args) { SpringApplication.run(GeoPointsApplication.class, args); }

	@Bean
	CommandLineRunner run() {
		return args -> {
			esTemplate.createIndex(GeoDocument.class);
			esTemplate.putMapping(GeoDocument.class);
			esTemplate.refresh(GeoDocument.class);
			geoPointsRepo.saveAll(generator.generateGeoPoints());
		};
	}

}
