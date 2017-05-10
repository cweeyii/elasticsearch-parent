package com.cweeyii.util;

import com.cweeyii.base.domain.EnterpriseBasicInfo;
import com.cweeyii.elastic.dto.EnterpriseBasicInfoDTO;
import org.springframework.beans.BeanUtils;

/**
 * Created by wenyi on 17/5/10.
 * Email:caowenyi@meituan.com
 */
public class ObjectConvertUtil {

    public static EnterpriseBasicInfoDTO convertBasicInfoDTO(EnterpriseBasicInfo enterpriseBasicInfo) {
        EnterpriseBasicInfoDTO enterpriseBasicInfoDTO = new EnterpriseBasicInfoDTO();
        BeanUtils.copyProperties(enterpriseBasicInfo, enterpriseBasicInfoDTO);
        return enterpriseBasicInfoDTO;
    }
}
