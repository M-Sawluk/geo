package com.michal.geo_points.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;
import com.michal.geo_points.model.GeoDocument;
import com.michal.geo_points.model.OpeningHours;

@Component
public class Generator {
	private Random generator = new Random();
	private static Map<Integer, List<String>> categroiesMap;
	static {
		categroiesMap = new HashMap<>();
		categroiesMap.put(0, Arrays.asList("Bankomat"));
		categroiesMap.put(1, Arrays.asList("Bankomat", "Kasa"));
		categroiesMap.put(2, Arrays.asList("Wpłatomat", "Kasa"));
	}

	public List<GeoDocument> generateGeoPoints() throws IOException, URISyntaxException {
		URL resource = this.getClass().getClassLoader().getResource("points.txt");
		List<String> points = Files.readAllLines(Paths.get(resource.toURI()));

		return points
				.stream()
				.map(point-> {
					OpeningHours openingHours = new OpeningHours(DayOfWeek.of(generator.nextInt(7) + 1).name(), generator.nextInt(25), generator.nextInt(25));
					String[] parts = point.split(",");
					GeoPoint geoPoint = GeoPoint.fromPoint(new Point(Double.parseDouble(parts[1]), Double.parseDouble(parts[3])));
					return new GeoDocument((long) points.indexOf(point),"Geo point "+String.valueOf(points.indexOf(point)), geoPoint, categroiesMap.get(generator.nextInt(3)), openingHours,"Lublin");
				})
				.collect(Collectors.toList());
	}
}
