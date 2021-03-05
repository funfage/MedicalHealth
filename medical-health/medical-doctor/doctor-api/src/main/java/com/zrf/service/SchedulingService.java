package com.zrf.service;

import com.zrf.domain.Scheduling;
import com.zrf.dto.SchedulingFormDto;
import com.zrf.dto.SchedulingQueryDto;

import java.util.List;

public interface SchedulingService {

    /**
     * 查询排班的数据
     * @param schedulingDto
     * @return
     */
    List<Scheduling> queryScheduling(SchedulingQueryDto schedulingDto);

    /**
     * 保存排班的数据
     * @param schedulingFormDto
     * @return
     */
    int saveScheduling(SchedulingFormDto schedulingFormDto);

    /**
     * 查询所有排班的部门信息
     * @param deptId
     * @param schedulingDay
     * @param schedulingType
     * @param subsectionType
     * @return
     */
    List<Long> queryHasSchedulingDeptIds(Long deptId, String schedulingDay, String schedulingType, String subsectionType);

}
