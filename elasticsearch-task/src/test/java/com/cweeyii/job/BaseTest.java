package com.cweeyii.job;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-datasource.xml","classpath:applicationContext-es.xml"})
public class BaseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        LOGGER.info("run setUpBeforeClass");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        LOGGER.info("run tearDownAfterClass");
    }

    @Test
    public void doNothing() {
        LOGGER.info("run doNothing");
    }
}