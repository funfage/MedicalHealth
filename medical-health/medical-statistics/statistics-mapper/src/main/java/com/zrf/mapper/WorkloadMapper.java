package com.zrf.mapper;

import com.zrf.domain.Workload;
import com.zrf.domain.WorkloadStat;
import com.zrf.dto.WorkloadQueryDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/15
 */
public interface WorkloadMapper {

    /**
     * 医生工作量统计列表
     *
     * @param workloadQueryDto
     * @return
     */
    List<Workload> queryWorkload(@Param("workload") WorkloadQueryDto workloadQueryDto);

    /**
     * 总体工作量统计列表
     *
     * @param workloadQueryDto
     * @return
     */
    List<WorkloadStat> queryWorkloadStat(@Param("workload") WorkloadQueryDto workloadQueryDto);

}
