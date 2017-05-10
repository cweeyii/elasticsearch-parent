package com.cweeyii.job;

import com.cweeyii.elastic.dto.EnterpriseBasicInfoDTO;
import com.cweeyii.elastic.handle.EnterpriseSearchHandle;
import com.cweeyii.operation.OperationBuilderFactory;
import com.cweeyii.operation.OperationType;
import com.cweeyii.operation.Pair;
import com.cweeyii.operation.SearchCondition;
import com.cweeyii.util.DistanceUtil;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by wenyi on 17/5/10.
 * Email:caowenyi@meituan.com
 */
public class ElasticSearchConditionTest extends BaseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchConditionTest.class);
    @Resource
    private EnterpriseSearchHandle enterpriseSearchHandle;

    @Test
    public void testFilterBuilder() {
        /*int times = 10000;
        SearchCondition filterCondition = new SearchCondition();
        filterCondition.setFilterBuilder(OperationBuilderFactory.builder().queryString("address", "云中", OperationType.MUST)
                .term("valid", 1, OperationType.MUST).builder());

        Long beginTime1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            List<EnterpriseBasicInfoDTO> basicInfoDTOList = enterpriseSearchHandle.getListByCondition(filterCondition, null);
        }
        Long beginTime2 = System.currentTimeMillis();
        LOGGER.info("运行Filter {} 花费时间{} 秒", times, (beginTime2 - beginTime1) / 1000);
        SearchCondition queryCondition = new SearchCondition();
        queryCondition.setQueryBuilder(OperationBuilderFactory.builder().queryString("address", "云中", OperationType.MUST).
                term("valid", 1, OperationType.MUST).builder());
        for (int i = 0; i < times; i++) {
            List<EnterpriseBasicInfoDTO> basicInfoDTOList = enterpriseSearchHandle.getListByCondition(queryCondition, null);
        }
        Long beginTime3 = System.currentTimeMillis();
        LOGGER.info("运行query {} 花费时间{} 秒", times, (beginTime3 - beginTime2) / 1000);*/

        /*SearchCondition filterCondition = new SearchCondition();
        filterCondition.setFilterBuilder(OperationBuilderFactory.builder().geoHash("location", 31.311987, 120.670569, 5, OperationType.MUST).builder())
                .orderBy("location", 31.311987, 120.670569, SortOrder.ASC, GeoDistance.ARC).orderBy("id", SortOrder.ASC).setOffset(0).setLimit(100);
        List<EnterpriseBasicInfoDTO> basicInfoDTOS = enterpriseSearchHandle.getListByCondition(filterCondition, null);
        for (EnterpriseBasicInfoDTO basicInfoDTO : basicInfoDTOS) {
            double distance = DistanceUtil.getDistance(basicInfoDTO.getLatitude(), basicInfoDTO.getLongitude(), 31.311987, 120.670569);
            LOGGER.info("{} {} {},{} to {},{} distance {} ", new Object[]{basicInfoDTO.getId(), basicInfoDTO.getDistrictName(), basicInfoDTO.getLatitude(), basicInfoDTO.getLongitude(), 31.311987, 120.670569, distance});
        }
        LOGGER.info("search total={} ", filterCondition.getTotal());*/

        double lat = 31.311987;
        double lon = 120.670569;
        SearchCondition filterCondition = new SearchCondition();
        filterCondition.setFilterBuilder(OperationBuilderFactory.builder().geoHash("location", lat, lon, 5, OperationType.MUST).builder())
                .orderBy("location", lat, lon, SortOrder.ASC, GeoDistance.ARC).orderBy("id", SortOrder.ASC).setOffset(0).setLimit(100);
        Pair<List<EnterpriseBasicInfoDTO>,Aggregations> basicInfoPair = enterpriseSearchHandle.getAggregationsByCondition(filterCondition, null);
        for (EnterpriseBasicInfoDTO basicInfoDTO : basicInfoPair.getFirst()) {
            double distance = DistanceUtil.getDistance(basicInfoDTO.getLatitude(), basicInfoDTO.getLongitude(), 31.311987, 120.670569);
            LOGGER.info("{} {} {},{} to {},{} distance {} ", new Object[]{basicInfoDTO.getId(), basicInfoDTO.getDistrictName(), basicInfoDTO.getLatitude(), basicInfoDTO.getLongitude(), 31.311987, 120.670569, distance});
        }

        LOGGER.info("search total={} ", filterCondition.getTotal());
    }
}
