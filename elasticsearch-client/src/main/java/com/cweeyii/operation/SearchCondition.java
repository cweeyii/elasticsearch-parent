package com.cweeyii.operation;

import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.geodistance.GeoDistanceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenyi on 17/5/9.
 * Email:caowenyi@meituan.com
 */
public class SearchCondition {
    private QueryBuilder queryBuilder = null;
    private QueryBuilder filterBuilder = null;
    private List<SortBuilder> orders = new ArrayList<>();
    private List<AbstractAggregationBuilder> aggregationBuilders = new ArrayList<>();

    private SearchType searchType;
    private int limit = 20;
    private int offset = 0;
    private int total = 0;

    public List<AbstractAggregationBuilder> getAggregationBuilders() {
        return aggregationBuilders;
    }

    public void setAggregationBuilders(List<AbstractAggregationBuilder> aggregationBuilders) {
        this.aggregationBuilders = aggregationBuilders;
    }

    public List<SortBuilder> getOrders() {
        return orders;
    }

    public void setOrders(List<SortBuilder> orders) {
        this.orders = orders;
    }

    public int getTotal() {
        return total;
    }

    public SearchCondition setTotal(int total) {
        this.total = total;
        return this;
    }

    public SearchCondition orderBy(String field, double lat, double lon, SortOrder order, GeoDistance geoDistance) {
        if (!StringUtils.isEmpty(field)) {
            orders.add(new GeoDistanceSortBuilder(field).order(order).point(lat, lon).geoDistance(geoDistance));
        }
        return this;
    }

    public SearchCondition orderBy(String field, double lat, double lon) {
        return orderBy(field, lat, lon, SortOrder.ASC, GeoDistance.DEFAULT);
    }

    public SearchCondition orderBy(String field, SortOrder order) {
        if (!StringUtils.isEmpty(field)) {
            orders.add(new FieldSortBuilder(field).order(order));
        }
        return this;
    }

    public SearchCondition orderBy(String field) {
        return orderBy(field, SortOrder.ASC);
    }

    public QueryBuilder getQueryBuilder() {
        if (queryBuilder == null) {
            return QueryBuilders.matchAllQuery();
        }
        return queryBuilder;
    }

    public SearchCondition setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
        return this;
    }

    public QueryBuilder getFilterBuilder() {
        return filterBuilder;
    }

    public SearchCondition setFilterBuilder(QueryBuilder filterBuilder) {
        this.filterBuilder = filterBuilder;
        return this;
    }

    public SearchCondition setAggregation(String field, double lat, double lon, Pair<Double, Double>... rangePoints) {
        if (!StringUtils.isEmpty(field)) {
            GeoDistanceBuilder geoDistanceBuilder = new GeoDistanceBuilder(field).point(new GeoPoint(lat, lon)).unit(DistanceUnit.METERS);
            for (Pair<Double, Double> rangePoint : rangePoints) {
                geoDistanceBuilder.addRange(rangePoint.getFirst(), rangePoint.getSecond());
            }
            aggregationBuilders.add(geoDistanceBuilder);
        }
        return this;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public SearchCondition setSearchType(SearchType searchType) {
        this.searchType = searchType;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public SearchCondition setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public int getOffset() {
        return offset;
    }

    public SearchCondition setOffset(int offset) {
        this.offset = offset;
        return this;
    }
}
