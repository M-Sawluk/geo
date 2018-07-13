package com.michal.geo_points.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.michal.geo_points.model.GeoBoxRequest;
import com.michal.geo_points.model.GeoDistanceRequest;
import com.michal.geo_points.model.GeoPoint;
import com.michal.geo_points.service.GeoPointsService;

@RestController
public class Controller {
	@Autowired
	private GeoPointsService geoPointsService;

	@PostMapping("/")
	public ResponseEntity<List<GeoPoint>> findByBox(@RequestBody GeoBoxRequest geoBoxRequest) {
		List<GeoPoint> inSquare = geoPointsService.findInSquare(geoBoxRequest);
		return new ResponseEntity<>(inSquare, HttpStatus.OK);
	}

	@PostMapping("/distance")
	public ResponseEntity<List<GeoPoint>> findByDistance(@RequestBody GeoDistanceRequest geoDistanceRequest) throws IOException {
		List<GeoPoint> inRange = geoPointsService.findInRange(geoDistanceRequest);
		return new ResponseEntity<>(inRange, HttpStatus.OK);
	}
}
