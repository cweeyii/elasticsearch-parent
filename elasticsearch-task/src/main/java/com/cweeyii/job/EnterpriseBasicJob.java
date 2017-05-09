package com.cweeyii.job;

import com.cweeyii.base.domain.EnterpriseBasicInfo;
import com.cweeyii.base.service.EnterpriseBasicInfoService;
import com.cweeyii.base.util.ScanDomainUtil;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by wenyi on 17/5/9.
 * Email:caowenyi@meituan.com
 */
@Component
public class EnterpriseBasicJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseBasicJob.class);

    @Resource
    private EnterpriseBasicInfoService enterpriseBasicInfoService;

    public void scanDataJob() {
        try {
            new ScanDomainUtil().scanDomain(enterpriseBasicInfoService, "scanByPage",
                    "getId", 0, 1000, EnterpriseBasicInfo.class, objList -> {
                        LOGGER.info("size={}", objList.size());
                        for (EnterpriseBasicInfo enterpriseBasicInfo : objList) {
                            LOGGER.info(ReflectionToStringBuilder.toString(enterpriseBasicInfo, ToStringStyle.SIMPLE_STYLE));
                        }
                    });
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
