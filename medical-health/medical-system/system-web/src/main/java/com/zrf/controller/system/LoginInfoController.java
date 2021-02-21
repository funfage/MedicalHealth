package com.zrf.controller.system;

import com.zrf.dto.LoginInfoDto;
import com.zrf.service.LoginInfoService;
import com.zrf.vo.AjaxResult;
import com.zrf.vo.DataGridView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 张润发
 * @date 2021/2/21
 */
@Log4j2
@RestController
@RequestMapping("system/loginInfo")
public class LoginInfoController {

    @Autowired
    private LoginInfoService loginInfoService;

    /**
     * 分页查询
     */
    @GetMapping("listForPage")
    public AjaxResult listForPage(LoginInfoDto loginInfoDto){
        DataGridView gridView = loginInfoService.listForPage(loginInfoDto);
        return AjaxResult.success("查询成功",gridView.getData(),gridView.getTotal());
    }

    /**
     * 删除
     */
    @DeleteMapping("deleteLoginInfoByIds/{infoIds}")
    public AjaxResult deleteLoginInfoByIds(@PathVariable Long[] infoIds){
        return AjaxResult.toAjax(loginInfoService.deleteLoginInfoByIds(infoIds));
    }
    /**
     * 清空删除
     */
    @DeleteMapping("clearLoginInfo")
    public AjaxResult clearLoginInfo(){
        return AjaxResult.toAjax(loginInfoService.clearLoginInfo());
    }
    
}
