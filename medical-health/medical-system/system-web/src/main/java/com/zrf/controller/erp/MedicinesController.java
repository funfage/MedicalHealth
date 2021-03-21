package com.zrf.controller.erp;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zaxxer.hikari.util.DriverDataSource;
import com.zrf.aspectj.annotation.Log;
import com.zrf.aspectj.enums.BusinessType;
import com.zrf.controller.BaseController;
import com.zrf.dto.MedicinesDto;
import com.zrf.service.MedicinesService;
import com.zrf.utils.ShiroSecurityUtils;
import com.zrf.vo.AjaxResult;
import com.zrf.vo.DataGridView;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author 张润发
 * @date 2021/2/24
 */
@RestController
@RequestMapping("erp/medicines")
public class MedicinesController extends BaseController {

    @Reference
    private MedicinesService medicinesService;

    @GetMapping("listMedicinesForPage")
    @HystrixCommand
    public AjaxResult listMedicinesForPage(MedicinesDto medicinesDto) {
        DataGridView gridView = medicinesService.listMedicinesPage(medicinesDto);
        return AjaxResult.success("查询成功", gridView.getData(), gridView.getTotal());
    }

    @PostMapping("addMedicines")
    @HystrixCommand
    @Log(title = "添加药品信息", businessType = BusinessType.INSERT)
    public AjaxResult addMedicines(@Validated MedicinesDto medicinesDto) {
        medicinesDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        return AjaxResult.toAjax(medicinesService.addMedicines(medicinesDto));
    }

    /**
     * 修改
     */
    @PutMapping("updateMedicines")
    @HystrixCommand
    @Log(title = "修改药品信息",businessType = BusinessType.UPDATE)
    public AjaxResult updateMedicines(@Validated MedicinesDto medicinesDto) {
        medicinesDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        return AjaxResult.toAjax(medicinesService.updateMedicines(medicinesDto));
    }


    /**
     * 根据ID查询一个药品信息信息
     */
    @GetMapping("getMedicinesById/{medicinesId}")
    @HystrixCommand
    public AjaxResult getMedicinesById(@PathVariable @Validated @NotNull(message = "药品信息ID不能为空") Long medicinesId) {
        return AjaxResult.success(medicinesService.getOne(medicinesId));
    }

    /**
     * 删除
     */
    @DeleteMapping("deleteMedicinesByIds/{medicinesIds}")
    @HystrixCommand
    @Log(title = "删除药品信息",businessType = BusinessType.DELETE)
    public AjaxResult deleteMedicinesByIds(@PathVariable @Validated @NotEmpty(message = "要删除的ID不能为空") Long[] medicinesIds) {
        return AjaxResult.toAjax(medicinesService.deleteMedicinesByIds(medicinesIds));
    }

    /**
     * 查询所有可用的药品信息
     */
    @GetMapping("selectAllMedicines")
    @HystrixCommand
    public AjaxResult selectAllMedicines() {
        return AjaxResult.success(medicinesService.selectAllMedicines());
    }

    /**
     * 调整库存
     */
    @PostMapping("updateMedicinesStorage/{medicinesId}/{medicinesStockNum}")
    @Log(title = "调整药品库存信息",businessType = BusinessType.UPDATE)
    @HystrixCommand
    public AjaxResult updateMedicinesStorage(@PathVariable Long medicinesId,@PathVariable Long medicinesStockNum){
        int i = medicinesService.updateMedicinesStorage(medicinesId, medicinesStockNum);
        return AjaxResult.toAjax(i);
    }

}
