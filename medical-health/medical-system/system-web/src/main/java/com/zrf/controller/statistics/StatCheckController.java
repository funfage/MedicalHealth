package com.zrf.controller.statistics;

import cn.hutool.core.date.DateUtil;
import com.zrf.controller.BaseController;
import com.zrf.dto.CheckQueryDto;
import com.zrf.service.CheckService;
import com.zrf.vo.AjaxResult;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 张润发
 * @date 2021/3/15
 */
@RestController
@RequestMapping("statistics/check")
public class StatCheckController extends BaseController {

    @Reference
    private CheckService checkService;

    /**
     * 查询检查项目列表
     */
    @GetMapping("queryCheck")
    public AjaxResult queryCheck(CheckQueryDto checkQueryDto){
        if(checkQueryDto.getBeginTime()==null){
            checkQueryDto.setQueryDate(DateUtil.format(DateUtil.date(),"yyyy-MM-dd"));
        }
        return AjaxResult.success(checkService.queryCheck(checkQueryDto));
    }

    /**
     * 查询检查项目列表
     */
    @GetMapping("queryCheckStat")
    public AjaxResult queryCheckStat(CheckQueryDto checkQueryDto){
        if(checkQueryDto.getBeginTime()==null){
            checkQueryDto.setQueryDate(DateUtil.format(DateUtil.date(),"yyyy-MM-dd"));
        }
        return AjaxResult.success(checkService.queryCheckStat(checkQueryDto));
    }

}
