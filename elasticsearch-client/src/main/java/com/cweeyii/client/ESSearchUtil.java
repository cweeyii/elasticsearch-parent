package com.cweeyii.client;

import com.cweeyii.operation.Pair;
import com.cweeyii.operation.SearchCondition;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Map;


public class ESSearchUtil {

    public static SearchResponse doSearch(Client client, SearchCondition searchCondition, String indexName, String indexType, String routing) {
        return buildRequest(client, searchCondition, indexName, indexType, routing).execute().actionGet();
    }

    public static SearchResponse doSearch(Client client, SearchCondition searchCondition, Long timeoutMillis, String indexName, String indexType, String routing) {
        return buildRequest(client, searchCondition, indexName, indexType, routing).execute().actionGet(timeoutMillis);
    }

    public static MultiSearchResponse doSearch(Client client, Map<SearchCondition, String> searchConditions2Routing, String indexName, String indexType) {
        MultiSearchRequestBuilder builder = client.prepareMultiSearch();
        for (Map.Entry<SearchCondition, String> entry : searchConditions2Routing.entrySet()) {
            SearchCondition searchCondition = entry.getKey();
            String routing = entry.getValue();
            builder.add(buildRequest(client, searchCondition, indexName, indexType, routing));
        }
        return builder.execute().actionGet();
    }

    public static SearchRequestBuilder buildRequest(Client client, SearchCondition searchCondition, String indexName, @Nullable String indexType, @Nullable String routing) {

        SearchRequestBuilder builder = client.prepareSearch(indexName).setQuery(searchCondition.getQueryBuilder())
                .setFrom(searchCondition.getOffset())
                .setSize(searchCondition.getLimit());
        if (searchCondition.getSearchType() != null) {
            searchCondition.setSearchType(searchCondition.getSearchType());
        }

        QueryBuilder postFilter = searchCondition.getFilterBuilder();
        if (postFilter != null) {
            builder.setPostFilter(postFilter);
        }

        if (routing != null) {
            builder.setRouting(routing);
        }

        if (searchCondition.getOrders() != null) {
            for (Pair<String, SortOrder> sorts : searchCondition.getOrders()) {
                builder.addSort(sorts.getFirst(), sorts.getSecond());
            }
        }
        return builder;
    }
}
