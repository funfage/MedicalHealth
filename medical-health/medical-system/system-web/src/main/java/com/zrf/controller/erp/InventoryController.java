package com.zrf.controller.erp;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zrf.controller.BaseController;
import com.zrf.dto.InventoryLogDto;
import com.zrf.service.InventoryLogService;
import com.zrf.vo.AjaxResult;
import com.zrf.vo.DataGridView;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 张润发
 * 入库记录控制器
 * @date 2021/2/27
 */
@RestController
@RequestMapping("erp/inventoryLog")
public class InventoryController extends BaseController {

    @Reference
    private InventoryLogService inventoryLogService;

    /**
     * 分页查询
     */
    @GetMapping("listInventoryLogForPage")
    @HystrixCommand
    public AjaxResult listMedicinesForPage(InventoryLogDto inventoryLogDto) {
        DataGridView gridView = this.inventoryLogService.listInventoryLogPage(inventoryLogDto);
        return AjaxResult.success("查询成功", gridView.getData(), gridView.getTotal());
    }

}
