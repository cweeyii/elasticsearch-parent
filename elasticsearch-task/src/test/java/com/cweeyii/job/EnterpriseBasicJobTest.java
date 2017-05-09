package com.cweeyii.job;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by wenyi on 17/5/9.
 * Email:caowenyi@meituan.com
 */
public class EnterpriseBasicJobTest extends BaseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseBasicJobTest.class);
    @Resource
    private EnterpriseBasicJob enterpriseBasicJob;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private EnterpriseBasicElasticJob enterpriseBasicElasticJob;

    @Test
    public void testFunction() {
        enterpriseBasicJob.scanDataJob();
    }

    @Test
    public void testJdbc() {
        String sql = "select enterprise_name from enterprise_basic_info where id>0 limit 10";
        List<String> names = jdbcTemplate.queryForList(sql, String.class);
        LOGGER.info(names.toString());
    }

    @Test
    public void testDTO(){
        enterpriseBasicElasticJob.scanIndexJob();
    }
}
