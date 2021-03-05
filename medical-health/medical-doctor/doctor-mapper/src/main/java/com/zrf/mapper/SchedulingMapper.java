package com.zrf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zrf.domain.Scheduling;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SchedulingMapper extends BaseMapper<Scheduling> {

    /**
     * 条件查询所有排班的部门信息
     *
     * @param deptId
     * @param schedulingDay
     * @param schedulingType
     * @param subsectionType
     * @return
     */
    List<Long> queryHasSchedulingDeptIds(@Param("deptId") Long deptId, @Param("schedulingDay") String schedulingDay, @Param("schedulingType") String schedulingType, @Param("subsectionType") String subsectionType);
}