package com.zrf.controller.erp;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zrf.aspectj.annotation.Log;
import com.zrf.aspectj.enums.BusinessType;
import com.zrf.controller.BaseController;
import com.zrf.dto.ProviderDto;
import com.zrf.service.ProviderService;
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
@RequestMapping("erp/provider")
public class ProviderController extends BaseController {

    @Reference
    private ProviderService providerService;

    /**
     * 分页查询
     */
    @GetMapping("listProviderForPage")
    @HystrixCommand
    public AjaxResult listProviderForPage(ProviderDto providerDto) {
        DataGridView gridView = this.providerService.listProviderPage(providerDto);
        return AjaxResult.success("查询成功", gridView.getData(), gridView.getTotal());
    }

    /**
     * 添加
     */
    @PostMapping("addProvider")
    @Log(title = "添加供应商", businessType = BusinessType.INSERT)
    @HystrixCommand
    public AjaxResult addProvider(@Validated ProviderDto providerDto) {
        providerDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        return AjaxResult.toAjax(this.providerService.addProvider(providerDto));
    }

    /**
     * 修改
     */
    @PutMapping("updateProvider")
    @Log(title = "修改供应商", businessType = BusinessType.UPDATE)
    @HystrixCommand
    public AjaxResult updateProvider(@Validated ProviderDto providerDto) {
        providerDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        return AjaxResult.toAjax(this.providerService.updateProvider(providerDto));
    }


    /**
     * 根据ID查询一个供应商信息
     */
    @GetMapping("getProviderById/{providerId}")
    @HystrixCommand
    public AjaxResult getProviderById(@PathVariable @Validated @NotNull(message = "供应商ID不能为空") Long providerId) {
        return AjaxResult.success(this.providerService.getOne(providerId));
    }

    /**
     * 删除
     */
    @DeleteMapping("deleteProviderByIds/{providerIds}")
    @Log(title = "删除供应商", businessType = BusinessType.DELETE)
    @HystrixCommand
    public AjaxResult deleteProviderByIds(@PathVariable @Validated @NotEmpty(message = "要删除的ID不能为空") Long[] providerIds) {
        return AjaxResult.toAjax(this.providerService.deleteProviderByIds(providerIds));
    }

    /**
     * 查询所有可用的供应商
     */
    @GetMapping("selectAllProvider")
    @HystrixCommand
    public AjaxResult selectAllProvider() {
        return AjaxResult.success(this.providerService.selectAllProvider());
    }

}
