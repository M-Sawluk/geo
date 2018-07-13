package com.michal.geo_points.model;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Document(indexName = "geo", type = "geo", shards = 1, replicas = 0, refreshInterval = "-1")
public class GeoDocument implements Comparable<GeoDocument>{
	@Id
	private Long id;
	@MultiField(
			mainField = @Field(type = FieldType.Text),
			otherFields = {
					@InnerField(suffix = "geoname", type = FieldType.Keyword)
			}
	)
	private String name;
	private GeoPoint point;
		@MultiField(
			mainField = @Field(type = FieldType.Text),
			otherFields = {
					@InnerField(suffix = "geoString", type = FieldType.Keyword)
			}
	)
	private List<String> categories;
	private OpeningHours openingHours;
	@MultiField(
			mainField = @Field(type = FieldType.Text),
			otherFields = {
					@InnerField(suffix = "geoAddress", type = FieldType.Keyword)
			}
	)
	private String address;

	public GeoDocument(Long id) { this.id = id; }

	@Override
	public int compareTo(GeoDocument other) { return this.id.compareTo(other.id);}

}
