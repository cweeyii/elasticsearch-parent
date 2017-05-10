package com.cweeyii.client;

import com.alibaba.fastjson.JSONObject;
import com.cweeyii.annotation.ESParams;
import com.cweeyii.operation.Pair;
import com.cweeyii.operation.SearchCondition;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wenyi on 17/5/9.
 * Email:caowenyi@meituan.com
 */
public abstract class SearchIndexService<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchIndexService.class);
    private ESConfig esConfig;
    private Client client;
    protected Class<T> clazz;

    public SearchIndexService(ESConfig esConfig) {
        client = ElasticSearchClientFactory.build(esConfig);
        clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.esConfig = esConfig;
    }

    public ESConfig getEsConfig() {
        return esConfig;
    }

    protected String getIndexName() {
        String indexName = null;
        Class<T> c = clazz;
        if (c.isAnnotationPresent(ESParams.class)) {
            indexName = c.getAnnotation(ESParams.class).indexName();
        }
        return indexName;
    }

    protected String getTypeName() {
        String typeName = null;
        Class<T> c = clazz;
        if (c.isAnnotationPresent(ESParams.class)) {
            typeName = c.getAnnotation(ESParams.class).typeName();
        }
        return typeName;
    }

    protected String getIndexId(T obj) {
        String indexId = null;
        try {
            Class<T> c = (Class<T>) obj.getClass();
            Method method = c.getMethod("getId", new Class[]{});
            indexId = method.invoke(obj, new Object[]{}).toString();
        } catch (Exception e) {
            LOGGER.warn("[get id failed]" + e.getMessage(), e);
        }
        return indexId;
    }

    public void updateIndexBulk(Map<T, String> obj2Routing) {
        if (obj2Routing == null || obj2Routing.isEmpty()) {
            LOGGER.info("elastic search bulk update param check failed");
            return;
        }
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (Map.Entry<T, String> entry : obj2Routing.entrySet()) {
            T obj = entry.getKey();
            String routing = entry.getValue();
            IndexRequestBuilder indexRequestBuilder = client.prepareIndex(getIndexName(), getTypeName(), getIndexId(obj)).setSource(JSONObject.toJSONString(obj));
            if (routing != null) {
                indexRequestBuilder.setRouting(routing);
            }
            bulkRequestBuilder.add(indexRequestBuilder);
        }
        bulkRequestBuilder.execute().actionGet();
    }

    public void deleteIndex(String indexId, @Nullable String routing) {
        if (StringUtils.isEmpty(indexId)) {
            LOGGER.info("delete es index indexId invalid.indexId=" + indexId);
            return;
        }
        DeleteRequestBuilder deleteRequestBuilder = client.prepareDelete(getIndexName(), getTypeName(), indexId);
        if (routing != null) {
            deleteRequestBuilder.setRouting(routing);
        }
        deleteRequestBuilder.execute().actionGet();
    }

    public List<List<T>> getListByCondition(Map<SearchCondition, String> searchCondition2Routing) {
        List<List<T>> results = new ArrayList<>();
        MultiSearchResponse multiSearchResponse = ESSearchUtil.doSearch(client, searchCondition2Routing, getIndexName(), getTypeName());
        for (MultiSearchResponse.Item item : multiSearchResponse.getResponses()) {
            List<T> objects = new ArrayList<>();
            SearchHits hits = item.getResponse().getHits();
            for (SearchHit searchHit : hits) {
                T obj = JSONObject.parseObject(searchHit.getSourceAsString(), clazz);
                if (obj == null) {
                    continue;
                }
                objects.add(obj);
            }
            results.add(objects);
        }
        return results;
    }

    public Pair<List<List<T>>, Long> getListAndHitsByCondition(Map<SearchCondition, String> searchCondition2Routing) {
        List<List<T>> results = new ArrayList<>();
        Long count = 0L;

        MultiSearchResponse multiSearchResponse = ESSearchUtil.doSearch(client, searchCondition2Routing, getIndexName(), getTypeName());
        for (MultiSearchResponse.Item item : multiSearchResponse.getResponses()) {
            List<T> objects = new ArrayList<>();
            SearchHits hits = item.getResponse().getHits();
            count += hits.getTotalHits();
            for (SearchHit searchHit : hits) {
                T obj = JSONObject.parseObject(searchHit.getSourceAsString(), clazz);
                if (obj == null) {
                    continue;
                }
                objects.add(obj);
            }
            results.add(objects);
        }

        return new Pair<>(results, count);
    }

    public List<T> getListByCondition(SearchCondition searchCondition, @Nullable String routing) {
        return getListByCondition(searchCondition, null, routing);
    }

    private SearchResponse getSearchResponseByCondition(SearchCondition searchCondition, Long timeoutMillis, @Nullable String routing) {
        SearchResponse searchResponse;

        if (timeoutMillis == null || timeoutMillis.intValue() <= 0) {
            searchResponse = ESSearchUtil.doSearch(client, searchCondition, getIndexName(), getTypeName(), routing);
        } else {
            searchResponse = ESSearchUtil.doSearch(client, searchCondition, timeoutMillis, getIndexName(), getTypeName(), routing);
        }
        return searchResponse;
    }

    public List<T> getListByCondition(SearchCondition searchCondition, Long timeoutMillis, @Nullable String routing) {
        List<T> objects = new ArrayList<>();
        SearchResponse searchResponse = getSearchResponseByCondition(searchCondition, timeoutMillis, routing);
        if (searchResponse == null) {
            return objects;
        }

        SearchHits hits = searchResponse.getHits();
        searchCondition.setTotal(Long.valueOf(hits.getTotalHits()).intValue());

        for (SearchHit searchHit : hits) {
            T obj = JSONObject.parseObject(searchHit.getSourceAsString(), clazz);
            if (obj == null) {
                continue;
            }
            objects.add(obj);
        }
        searchResponse.getAggregations();
        return objects;
    }

    public Pair<List<T>, Aggregations> getAggregationsByCondition(SearchCondition searchCondition, @Nullable String routing) {
        return getAggregationsByCondition(searchCondition,null,routing);
    }

    public Pair<List<T>, Aggregations> getAggregationsByCondition(SearchCondition searchCondition, Long timeoutMillis, @Nullable String routing) {
        List<T> objects = new ArrayList<>();
        SearchResponse searchResponse = getSearchResponseByCondition(searchCondition, timeoutMillis, routing);

        if (searchResponse == null) {
            return null;
        }

        SearchHits hits = searchResponse.getHits();
        searchCondition.setTotal(Long.valueOf(hits.getTotalHits()).intValue());

        for (SearchHit searchHit : hits) {
            T obj = JSONObject.parseObject(searchHit.getSourceAsString(), clazz);
            if (obj == null) {
                continue;
            }
            objects.add(obj);
        }
        Aggregations aggregations = searchResponse.getAggregations();
        return new Pair<>(objects, aggregations);
    }

    protected abstract String routingRule();
}
