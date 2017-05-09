package com.cweeyii.base.service;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by wenyi on 17/5/9.
 * Email:caowenyi@meituan.com
 */
public interface BaseOperateService<T> {
    default List<T> scanByPage(Long begId, int pageSize) {
        return Lists.newArrayList();
    }
}
