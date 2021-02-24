package com.zrf.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.zrf.vo.AjaxResult;

/**
 * @author 张润发
 * @date 2021/2/24
 */
@DefaultProperties(defaultFallback = "fallback")
public class BaseController {

    /**
     * 如远程服务不可用，或者出现异常，回调的方法
     * @return
     */
    public AjaxResult fallback(){
        return AjaxResult.toAjax(-1);
    }

}
