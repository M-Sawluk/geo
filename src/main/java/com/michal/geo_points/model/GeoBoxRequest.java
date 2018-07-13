package com.michal.geo_points.model;

import lombok.Data;

@Data
public class GeoBoxRequest {
	private int precision;
	private double latL;
	private double lonL;
	private double latP;
	private double lonP;
}
