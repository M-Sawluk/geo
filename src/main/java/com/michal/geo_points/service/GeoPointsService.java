package com.michal.geo_points.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.GeoBoundingBoxQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoGridAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoHashGrid;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import com.michal.geo_points.model.GeoBoxRequest;
import com.michal.geo_points.model.GeoDistanceRequest;

@Service
public class GeoPointsService {
	@Autowired
	private ElasticsearchTemplate esTemplate;

	public List<com.michal.geo_points.model.GeoPoint> findInSquare(GeoBoxRequest geoReq) {
		GeoGridAggregationBuilder agg = AggregationBuilders
				.geohashGrid("geoHashAggregation")
				.field("point")
				.precision(geoReq.getPrecision())
				.subAggregation(AggregationBuilders
						.terms("nameAggregation")
						.field("name.geoname"))
				.subAggregation(AggregationBuilders
						.terms("addressAggregation")
						.field("address.geoAddress")
						.subAggregation(AggregationBuilders
								.terms("idAggregation")
								.field("id")));

		GeoBoundingBoxQueryBuilder boxQuery = QueryBuilders
				.geoBoundingBoxQuery("point")
				.setCorners(new GeoPoint(geoReq.getLatL(), geoReq.getLonL()), new GeoPoint(geoReq.getLatP(), geoReq.getLonP()));

		SearchResponse searchResponse = esTemplate
				.getClient()
				.prepareSearch()
				.setIndices("geo")
				.setQuery(boxQuery)
				.addAggregation(agg)
				.get();

//		List<GeoDocument> geoDocuments = searchHitsToGeoDocs(searchResponse.getHits().getHits());

		List<com.michal.geo_points.model.GeoPoint> geoPoints = new ArrayList<>();
		GeoHashGrid geoHashAggregation = searchResponse
				.getAggregations()
				.get("geoHashAggregation");

		for (GeoHashGrid.Bucket bucket : geoHashAggregation.getBuckets()) {
			Terms namesAggregations = bucket
					.getAggregations()
					.get("nameAggregation");
			GeoPoint point = (GeoPoint) bucket.getKey();
			if (bucket.getDocCount() > 1) {
				StringBuilder name = new StringBuilder();
				for (Terms.Bucket nameBuckets : namesAggregations.getBuckets()) {
					name.append(nameBuckets.getKey()).append(" ");
				}
				name.append(String.valueOf(bucket.getDocCount()));
				com.michal.geo_points.model.GeoPoint geoPoint = new com.michal.geo_points.model.GeoPoint(name.toString(), point.getLat(), point.getLon(), 0.0);
				geoPoints.add(geoPoint);
			} else {
				Terms addressBucket = geoHashAggregation.getBuckets().get(0).getAggregations().get("addressAggregation");
				com.michal.geo_points.model.GeoPoint geoPoint =
						new com.michal.geo_points.model.GeoPoint(addressBucket.getBuckets().get(0).getKey().toString(), point.getLat(), point.getLon(), 0.0);
				geoPoints.add(geoPoint);
			}
		}

		return geoPoints;
	}

	public List<com.michal.geo_points.model.GeoPoint> findInRange(GeoDistanceRequest geoDistanceRequest) throws IOException {
		GeoDistanceQueryBuilder distanceQuery = QueryBuilders
				.geoDistanceQuery("point")
				.point(51.239224, 22.525136)
				.distance(0.5, DistanceUnit.KILOMETERS);

		GeoDistanceSortBuilder ascDistanceSort = SortBuilders
				.geoDistanceSort("point", 51.239224, 22.525136)
				.order(SortOrder.ASC);

		XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
				.startObject()
				.startObject("query")
					.startObject("geo_distance")
						.array("point", Arrays.asList(22.525136, 51.239224).toArray())
						.field("distance", 500)
						.field("distance_type", "arc")
					.endObject()
				.endObject()
				.startArray("sort")
					.startObject()
						.startObject("_geo_distance")
							.array("point", Arrays.asList(22.525136, 51.239224).toArray())
							.field("order", "asc")
							.field("unit", "km")
							.field("mode", "min")
							.field("distance_type", "arc")
						.endObject()
					.endObject()
				.endArray()
				.endObject();

		String json = " {\n" +
				"    \"geo_distance\" : {\n" +
				"      \"point\" : [\n" +
				"        22.525136,\n" +
				"        51.239224\n" +
				"      ],\n" +
				"      \"distance\" : 500.0,\n" +
				"      \"distance_type\" : \"arc\"\n" +
				"    }\n" +
				"  }";

		WrapperQueryBuilder wrapperQueryBuilder = QueryBuilders.wrapperQuery(json);

		SearchResponse geo = esTemplate
				.getClient()
				.prepareSearch("geo")
				.setTypes("geo")
				.setQuery(wrapperQueryBuilder)
				.addSort(ascDistanceSort)
				.get();

		return null;
	}



//	private List<GeoDocument> searchHitsToGeoDocs(SearchHit[] hits) {
//		return Stream
//				.of(hits)
//				.map(hit -> {
//					long id = Long.parseLong(hit.getId());
//					String name = hit.getField("name.geoname").getValue();
//					String address = hit.getField("address.geoAddress").getValue();
//
//					return new GeoDocument(id, name, null, null, null, address);
//				})
//				.collect(Collectors.toList());
//	}
//
//	private GeoDocument findGeoDoc(
//			Object id,
//			List<GeoDocument> geoDocuments
//	) {
//		GeoDocument geoDocument = new GeoDocument((Long) id);
//		int index = Collections.binarySearch(geoDocuments, geoDocument);
//		return geoDocuments.get(index);
//	}

	//		for (GeoHashGrid.Bucket bucket : geoHashAggregation.getBuckets()) {
//			long docCount = bucket.getDocCount();
//			Terms idTerm = bucket.getAggregations().get("idAgg");
//			List<? extends Terms.Bucket> idBuckets = idTerm.getBuckets();
//			GeoPoint point = (GeoPoint) bucket.getKey();
//			com.michal.geo_points.model.GeoPoint geoPoint;
//			if(docCount > 1) {
//				StringBuilder name = new StringBuilder();
//				for(int i = 0 ; i < idBuckets.size() && i < 3; i++) {
//					GeoDocument geoDocForName = findGeoDoc(idBuckets.get(i).getKey(), geoDocuments);
//					name.append(geoDocForName.getName());
//					name.append(" ");
//				}
//				name.append("Punktów: ");
//				name.append(String.valueOf(docCount));
//				geoPoint = new com.michal.geo_points.model.GeoPoint(name.toString(), point.getLat(), point.getLon());
//			} else {
//				GeoDocument geoDoc = findGeoDoc(idBuckets.get(0).getKey(), geoDocuments);
//				geoPoint = new com.michal.geo_points.model.GeoPoint(geoDoc.getName() + " " + geoDoc.getAddress(), point.getLat(), point.getLon());
//			}
//			geoPoints.add(geoPoint);
//		}
}
