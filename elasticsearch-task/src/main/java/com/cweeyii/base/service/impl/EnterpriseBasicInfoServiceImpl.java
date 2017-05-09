package com.cweeyii.base.service.impl;

import com.cweeyii.base.dao.mapper.EnterpriseBasicInfoMapper;
import com.cweeyii.base.domain.EnterpriseBasicInfo;
import com.cweeyii.base.service.EnterpriseBasicInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by wenyi on 17/5/9.
 * Email:caowenyi@meituan.com
 */
@Service
public class EnterpriseBasicInfoServiceImpl implements EnterpriseBasicInfoService {
    @Resource
    private EnterpriseBasicInfoMapper enterpriseBasicInfoMapper;

    @Override
    public List<EnterpriseBasicInfo> scanByPage(Long begId, int pageSize) {
        return enterpriseBasicInfoMapper.selectByPage(begId, pageSize);
    }
}
