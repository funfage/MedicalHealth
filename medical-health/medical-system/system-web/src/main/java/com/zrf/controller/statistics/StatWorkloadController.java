package com.zrf.controller.statistics;

import cn.hutool.core.date.DateUtil;
import com.zrf.controller.BaseController;
import com.zrf.domain.Workload;
import com.zrf.domain.WorkloadStat;
import com.zrf.dto.WorkloadQueryDto;
import com.zrf.service.WorkloadService;
import com.zrf.vo.AjaxResult;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/15
 * 工作量计算控制器
 */
@RestController
@RequestMapping("statistics/workload")
public class StatWorkloadController extends BaseController {


    @Reference
    private WorkloadService workloadService;

    /**
     * 医生工作量统计列表
     */
    @GetMapping("queryWorkload")
    public AjaxResult queryWorkload(WorkloadQueryDto workloadQueryDto){
        if(workloadQueryDto.getBeginTime()==null){
            workloadQueryDto.setQueryDate(DateUtil.format(DateUtil.date(),"yyyy-MM-dd"));
        }
        List<Workload> workloadList=this.workloadService.queryWorkload(workloadQueryDto);
        return AjaxResult.success(workloadList);
    }


    /**
     * 总体工作量统计列表
     */
    @GetMapping("queryWorkloadStat")
    public AjaxResult queryWorkloadStat(WorkloadQueryDto workloadQueryDto){
        if(workloadQueryDto.getBeginTime()==null){
            workloadQueryDto.setQueryDate(DateUtil.format(DateUtil.date(),"yyyy-MM-dd"));
        }
        List<WorkloadStat> workloadList=this.workloadService.queryWorkloadStat(workloadQueryDto);
        return AjaxResult.success(workloadList);
    }

}
