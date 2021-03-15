package com.zrf.service.impl;

import com.zrf.domain.Workload;
import com.zrf.domain.WorkloadStat;
import com.zrf.dto.WorkloadQueryDto;
import com.zrf.mapper.WorkloadMapper;
import com.zrf.service.WorkloadService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/15
 */
@Service
public class WorkloadServiceImpl implements WorkloadService {

    @Autowired
    private WorkloadMapper workloadMapper;

    @Override
    public List<Workload> queryWorkload(WorkloadQueryDto workloadQueryDto) {
        return this.workloadMapper.queryWorkload(workloadQueryDto);
    }

    @Override
    public List<WorkloadStat> queryWorkloadStat(WorkloadQueryDto workloadQueryDto) {
        return this.workloadMapper.queryWorkloadStat(workloadQueryDto);
    }

}
