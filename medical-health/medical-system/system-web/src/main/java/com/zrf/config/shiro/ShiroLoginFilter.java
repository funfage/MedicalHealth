package com.zrf.config.shiro;

import com.alibaba.fastjson.JSON;
import com.zrf.constants.HttpStatus;
import com.zrf.vo.AjaxResult;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 张润发
 * 判断是否进行了登录
 * @date 2021/2/18
 */
public class ShiroLoginFilter extends FormAuthenticationFilter {

    /**
     * 在访问controller前判断是否登录，返回json，不进行重定向
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        AjaxResult ajaxResult = AjaxResult.fail();
        ajaxResult.put("code", HttpStatus.UNAUTHORIZED);
        ajaxResult.put("msg", "登录认证失败，请重新登录！");
        httpServletResponse.getWriter().write(JSON.toJSON(ajaxResult).toString());
        return false;
    }
}
