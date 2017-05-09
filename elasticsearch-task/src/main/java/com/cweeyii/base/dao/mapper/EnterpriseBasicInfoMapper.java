package com.cweeyii.base.dao.mapper;

import com.cweeyii.base.domain.EnterpriseBasicInfo;
import com.cweeyii.base.domain.EnterpriseBasicInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EnterpriseBasicInfoMapper {
    int countByExample(EnterpriseBasicInfoExample example);

    long insert(EnterpriseBasicInfo record);

    long insertSelective(EnterpriseBasicInfo record);

    List<EnterpriseBasicInfo> selectByExample(EnterpriseBasicInfoExample example);

    EnterpriseBasicInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EnterpriseBasicInfo record, @Param("example") EnterpriseBasicInfoExample example);

    int updateByExample(@Param("record") EnterpriseBasicInfo record, @Param("example") EnterpriseBasicInfoExample example);

    int updateByPrimaryKeySelective(EnterpriseBasicInfo record);

    int updateByPrimaryKey(EnterpriseBasicInfo record);

    List<EnterpriseBasicInfo> selectByPage(@Param("begId") Long begId,@Param("pageSize") int pageSize);
}