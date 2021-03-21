package com.zrf.controller.erp;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zrf.aspectj.annotation.Log;
import com.zrf.aspectj.enums.BusinessType;
import com.zrf.dto.ProducterDto;
import com.zrf.service.ProducterService;
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
@RequestMapping("erp/producter")
public class ProducterController {

    @Reference
    private ProducterService producterService;

    /**
     * 分页查询
     */
    @GetMapping("listProducterForPage")
    @HystrixCommand
    public AjaxResult listProducterForPage(ProducterDto producterDto){
        DataGridView gridView = producterService.listProducterPage(producterDto);
        return AjaxResult.success("查询成功",gridView.getData(),gridView.getTotal());
    }
    /**
     * 添加
     */
    @PostMapping("addProducter")
    @Log(title = "添加生产厂家",businessType = BusinessType.INSERT)
    @HystrixCommand
    public AjaxResult addProducter(@Validated ProducterDto producterDto) {
        producterDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        return AjaxResult.toAjax(producterService.addProducter(producterDto));
    }

    /**
     * 修改
     */
    @PutMapping("updateProducter")
    @Log(title = "修改生产厂家",businessType = BusinessType.UPDATE)
    @HystrixCommand
    public AjaxResult updateProducter(@Validated ProducterDto producterDto) {
        producterDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        return AjaxResult.toAjax(producterService.updateProducter(producterDto));
    }


    /**
     * 根据ID查询一个生产厂家信息
     */
    @GetMapping("getProducterById/{producterId}")
    @HystrixCommand
    public AjaxResult getProducterById(@PathVariable @Validated @NotNull(message = "生产厂家ID不能为空") Long producterId) {
        return AjaxResult.success(producterService.getOne(producterId));
    }

    /**
     * 删除
     */
    @DeleteMapping("deleteProducterByIds/{producterIds}")
    @Log(title = "删除生产厂家",businessType = BusinessType.DELETE)
    @HystrixCommand
    public AjaxResult deleteProducterByIds(@PathVariable @Validated @NotEmpty(message = "要删除的ID不能为空") Long[] producterIds) {
        return AjaxResult.toAjax(producterService.deleteProducterByIds(producterIds));
    }

    /**
     * 查询所有可用的生产厂家
     */
    @GetMapping("selectAllProducter")
    @HystrixCommand
    public AjaxResult selectAllProducter() {
        return AjaxResult.success(producterService.selectAllProducter());
    }

}
