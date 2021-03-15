package com.zrf.controller.doctor;

import cn.hutool.core.date.DateUtil;
import com.zrf.constants.Constants;
import com.zrf.controller.BaseController;
import com.zrf.domain.Drug;
import com.zrf.dto.DrugQueryDto;
import com.zrf.service.DrugService;
import com.zrf.vo.AjaxResult;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/15
 * 药品销售统计控制器
 */
@RestController
@RequestMapping("statistics/drug")
public class StatDrugController extends BaseController {

    @Reference
    private DrugService drugService;

    /**
     * 查询发药统计列表
     */
    @GetMapping("queryDrug")
    public AjaxResult queryDrug(DrugQueryDto drugQueryDto) {
        // 如果查询日期为空则默认查询当天日期
        if (drugQueryDto.getBeginTime() == null) {
            drugQueryDto.setQueryDate(DateUtil.format(DateUtil.date(), Constants.DATE_FORMATTER));
        }
        List<Drug> drugList = drugService.queryDrug(drugQueryDto);
        return AjaxResult.success(drugList);
    }

    /**
     * 查询发药数量统计列表
     */
    @GetMapping("queryDrugStat")
    public AjaxResult queryDrugStat(DrugQueryDto drugQueryDto) {
        // 如果查询日期为空则默认查询当天日期
        if (drugQueryDto.getBeginTime() == null) {
            drugQueryDto.setQueryDate(DateUtil.format(DateUtil.date(), Constants.DATE_FORMATTER));
        }
        List<Drug> drugList = drugService.queryDrugStat(drugQueryDto);
        return AjaxResult.success(drugList);
    }


}
