package com.michal.geo_points.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OpeningHours {
	private String day;
	private int open;
	private int closed;
}
