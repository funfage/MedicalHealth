package com.zrf.service;

import com.zrf.domain.Workload;
import com.zrf.domain.WorkloadStat;
import com.zrf.dto.WorkloadQueryDto;

import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/15
 */
public interface WorkloadService {

    /**
     * 医生工作量统计列表
     * @param workloadQueryDto
     * @return
     */
    List<Workload> queryWorkload(WorkloadQueryDto workloadQueryDto);

    /**
     * 总体工作量统计列表
     * @param workloadQueryDto
     * @return
     */
    List<WorkloadStat> queryWorkloadStat(WorkloadQueryDto workloadQueryDto);
}
