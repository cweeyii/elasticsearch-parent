package com.cweeyii.operation;

import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenyi on 17/5/9.
 * Email:caowenyi@meituan.com
 */
public class SearchCondition {
    private QueryBuilder queryBuilder=null;
    private QueryBuilder filterBuilder=null;
    private List<Pair<String, SortOrder>> orders = new ArrayList<Pair<String, SortOrder>>();

    private SearchType searchType;
    private int limit = 20;
    private int offset = 0;
    private int total=0;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public SearchCondition orderBy(String key, SortOrder order) {
        if (StringUtils.isEmpty(key)) {
            orders.add(new Pair<String, SortOrder>(key, order));
        }
        return this;
    }

    public List<Pair<String, SortOrder>> getOrders() {
        return orders;
    }

    public void setOrders(List<Pair<String, SortOrder>> orders) {
        this.orders = orders;
    }

    public QueryBuilder getQueryBuilder() {
        if(queryBuilder==null){
            return QueryBuilders.matchAllQuery();
        }
        return queryBuilder;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public QueryBuilder getFilterBuilder() {
        return filterBuilder;
    }

    public void setFilterBuilder(QueryBuilder filterBuilder) {
        this.filterBuilder = filterBuilder;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
