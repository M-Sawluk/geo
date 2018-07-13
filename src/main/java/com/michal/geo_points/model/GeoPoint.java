package com.michal.geo_points.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeoPoint {
	private String name;
	private double lat;
	private double lon;
	private double distance;
}
