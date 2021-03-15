package com.zrf.service;

import com.zrf.dto.RevenueQueryDto;

import java.util.Map;

/**
 * @author 张润发
 * @date 2021/3/13
 */
public interface RevenueService {

    /**
     * 查询收支统计数据
     * @param revenueQueryDto
     * @return
     */
    Map<String, Object> queryAllRevenueData(RevenueQueryDto revenueQueryDto);
}
