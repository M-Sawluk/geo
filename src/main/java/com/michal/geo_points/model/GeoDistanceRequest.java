package com.michal.geo_points.model;

import lombok.Data;

@Data
public class GeoDistanceRequest {
	private double distance;
	private double lat;
	private double lon;
}
