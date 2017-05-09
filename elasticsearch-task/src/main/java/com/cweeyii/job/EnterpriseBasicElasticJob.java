package com.cweeyii.job;

import com.cweeyii.base.domain.EnterpriseBasicInfo;
import com.cweeyii.base.service.EnterpriseBasicInfoService;
import com.cweeyii.base.util.ScanDomainUtil;
import com.cweeyii.elastic.dto.EnterpriseBasicInfoDTO;
import com.cweeyii.elastic.handle.EnterpriseSearchHandle;
import com.cweeyii.util.ObjectConvertUtil;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wenyi on 17/5/10.
 * Email:caowenyi@meituan.com
 */
@Component
public class EnterpriseBasicElasticJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseBasicElasticJob.class);
    @Resource
    private EnterpriseBasicInfoService enterpriseBasicInfoService;
    @Resource
    private EnterpriseSearchHandle enterpriseSearchHandle;

    public void scanIndexJob() {
        try {
            new ScanDomainUtil().scanDomain(enterpriseBasicInfoService, "scanByPage",
                    "getId", 0, 1000, EnterpriseBasicInfo.class, objList -> {
                        Map<EnterpriseBasicInfoDTO, String> dtoMap = new HashMap<>();
                        for (EnterpriseBasicInfo enterpriseBasicInfo : objList) {
                            EnterpriseBasicInfoDTO enterpriseBasicInfoDTO = ObjectConvertUtil.convertBasicInfoDTO(enterpriseBasicInfo);
                            dtoMap.put(enterpriseBasicInfoDTO, enterpriseBasicInfoDTO.getCityName());
                        }
                        if (!CollectionUtils.isEmpty(dtoMap)) {
                            enterpriseSearchHandle.updateIndexBulk(dtoMap);
                        }
                    });
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


}
