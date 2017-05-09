package com.cweeyii.util;

import com.cweeyii.base.domain.EnterpriseBasicInfo;
import com.cweeyii.elastic.dto.EnterpriseBasicInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wenyi on 17/5/10.
 * Email:caowenyi@meituan.com
 */
public class ObjectConvertUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectConvertUtil.class);
    private static final Map<String, BeanCopier> COPIER_MAP = new ConcurrentHashMap<String, BeanCopier>();

    public static void copyProperties(Object src, Object dst) {
        if (src == null || dst == null) {
            return;
        }
        try {
            String key = src.getClass().getName() + "-" + dst.getClass().getName();
            BeanCopier coper = COPIER_MAP.get(key);
            if (coper == null) {
                synchronized (ObjectConvertUtil.class) {
                    if (coper == null) {
                        coper = BeanCopier.create(src.getClass(), dst.getClass(), false);
                        COPIER_MAP.put(key, coper);
                    }
                }
            }

            coper.copy(src, dst, null);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public static EnterpriseBasicInfoDTO convertBasicInfoDTO(EnterpriseBasicInfo enterpriseBasicInfo) {
        EnterpriseBasicInfoDTO enterpriseBasicInfoDTO=new EnterpriseBasicInfoDTO();
        copyProperties(enterpriseBasicInfo,enterpriseBasicInfoDTO);
        return enterpriseBasicInfoDTO;
    }
}
