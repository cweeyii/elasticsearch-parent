package com.cweeyii.operation;

import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.*;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wenyi on 17/5/9.
 * Email:caowenyi@meituan.com
 */
public class OperationBuilderFactory {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Map<OperationType, List<QueryBuilder>> queryBuilderMap = new ConcurrentHashMap<>();

        private Builder(){}

        public Builder term(String field, Object value, OperationType operationType) {
            List<QueryBuilder> queryBuilders = getQueryBuilders(operationType);
            queryBuilders.add(new TermQueryBuilder(field, value));
            return this;
        }

        public Builder queryString(String field, String value, OperationType operationType, QueryStringQueryBuilder.Operator operator) {
            List<QueryBuilder> queryBuilders = getQueryBuilders(operationType);
            queryBuilders.add(new QueryStringQueryBuilder(value).field(field).defaultOperator(operator));
            return this;
        }

        public Builder queryString(String field, String value, OperationType operationType) {
            return queryString(field, value, operationType, QueryStringQueryBuilder.Operator.OR);
        }

        public Builder prefix(String field, String prefix, OperationType operationType) {
            List<QueryBuilder> queryBuilders = getQueryBuilders(operationType);
            queryBuilders.add(new PrefixQueryBuilder(field, prefix));
            return this;
        }

        public Builder range(String field, Object from, Object to, OperationType operationType) {
            List<QueryBuilder> queryBuilders = getQueryBuilders(operationType);
            queryBuilders.add(new RangeQueryBuilder(field).from(from).to(to));
            return this;
        }

        public Builder notInId(List<String> ids, OperationType operationType) {
            List<QueryBuilder> queryBuilders = getQueryBuilders(operationType);
            queryBuilders.add(new IdsQueryBuilder().ids(ids));
            return this;
        }

        public Builder fuzzy(String field, Object value, OperationType operationType) {
            List<QueryBuilder> queryBuilders = getQueryBuilders(operationType);
            queryBuilders.add(new FuzzyQueryBuilder(field, value));
            return this;
        }

        public Builder wildcard(String field, String value, OperationType operationType) {
            List<QueryBuilder> queryBuilders = getQueryBuilders(operationType);
            queryBuilders.add(new WildcardQueryBuilder(field, value));
            return this;
        }

        public Builder geoDistance(String field, double lat, double lon, double distance, OperationType operationType) {
            List<QueryBuilder> queryBuilders = getQueryBuilders(operationType);
            queryBuilders.add(new GeoDistanceQueryBuilder(field).point(lat, lon).distance(distance, DistanceUnit.METERS));
            return this;
        }

        public Builder geoHash(String field, double lat, double lon, int precisionLevel, OperationType operationType) {
            List<QueryBuilder> queryBuilders = getQueryBuilders(operationType);
            queryBuilders.add(new GeohashCellQuery.Builder(field).point(lat, lon).precision(precisionLevel).neighbors(true));
            return this;
        }

        public QueryBuilder builder() {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            List<QueryBuilder> mustBuilders = getQueryBuilders(OperationType.MUST);
            if (!CollectionUtils.isEmpty(mustBuilders)) {
                for (QueryBuilder queryBuilder : mustBuilders) {
                    boolQueryBuilder.must(queryBuilder);
                }
            }
            List<QueryBuilder> mustNotBuilders = getQueryBuilders(OperationType.MUST_NOT);
            if (!CollectionUtils.isEmpty(mustNotBuilders)) {
                for (QueryBuilder queryBuilder : mustNotBuilders) {
                    boolQueryBuilder.mustNot(queryBuilder);
                }
            }
            List<QueryBuilder> shouldBuilders = getQueryBuilders(OperationType.SHOULD);
            if (!CollectionUtils.isEmpty(shouldBuilders)) {
                for (QueryBuilder queryBuilder : shouldBuilders) {
                    boolQueryBuilder.should(queryBuilder);
                }
            }
            return boolQueryBuilder;
        }

        public List<QueryBuilder> getQueryBuilders(OperationType operationType) {
            List<QueryBuilder> queryBuilders = queryBuilderMap.get(operationType);
            if (queryBuilders == null) {
                synchronized (this) {
                    if (queryBuilders == null) {
                        queryBuilders = new ArrayList<>();
                        queryBuilderMap.put(operationType, queryBuilders);
                    }
                }
            }
            return queryBuilders;
        }
    }


}
