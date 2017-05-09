package com.cweeyii.elastic.handle;

import com.cweeyii.client.ESConfig;
import com.cweeyii.client.SearchIndexService;
import com.cweeyii.elastic.dto.EnterpriseBasicInfoDTO;

/**
 * Created by wenyi on 17/5/10.
 * Email:caowenyi@meituan.com
 */
public class EnterpriseSearchHandle extends SearchIndexService<EnterpriseBasicInfoDTO> {

    public EnterpriseSearchHandle(ESConfig esConfig) {
        super(esConfig);
    }

    @Override
    protected String routingRule() {
        return "cityName做为路由信息";
    }

    @Override
    protected String getIndexId(EnterpriseBasicInfoDTO obj) {
        return String.valueOf(obj.getId());
    }
}
