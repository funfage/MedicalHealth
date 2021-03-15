package com.zrf.service;

import com.zrf.domain.Check;
import com.zrf.domain.CheckStat;
import com.zrf.dto.CheckQueryDto;

import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/15
 */
public interface CheckService {

    /**
     * 查询检查项目列表
     * @param checkQueryDto
     * @return
     */
    List<Check> queryCheck(CheckQueryDto checkQueryDto);

    /**
     * 查询检查项统计列表
     * @param checkQueryDto
     * @return
     */
    List<CheckStat> queryCheckStat(CheckQueryDto checkQueryDto);

}
