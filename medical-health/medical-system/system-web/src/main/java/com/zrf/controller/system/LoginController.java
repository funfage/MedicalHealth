package com.zrf.controller.system;

import com.zrf.aspectj.annotation.Log;
import com.zrf.aspectj.enums.BusinessType;
import com.zrf.constants.Constants;
import com.zrf.constants.HttpStatus;
import com.zrf.domain.Menu;
import com.zrf.domain.SimpleUser;
import com.zrf.dto.LoginBodyDto;
import com.zrf.service.MenuService;
import com.zrf.vo.ActiverUser;
import com.zrf.vo.AjaxResult;
import com.zrf.vo.MenuTreeVo;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张润发
 * @date 2021/2/18
 */
@RestController
@Log4j2
public class LoginController {

    @Autowired
    private MenuService menuService;

    /**
     * 登录方法
     *
     * @param loginBodyDto
     * @param request
     * @return
     */
    @PostMapping("login/doLogin")
    @Log(title = "用户登录", businessType = BusinessType.OTHER)
    public AjaxResult login(@RequestBody @Validated LoginBodyDto loginBodyDto, HttpServletRequest request) {
        AjaxResult result = AjaxResult.success();
        String username = loginBodyDto.getUsername();
        String password = loginBodyDto.getPassword();
        // 构造用户名和密码的token
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            // 调用shiro的登录进行认证
            subject.login(token);
            // 得到会话的token，也就是redis里存的
            Serializable webToken = subject.getSession().getId();
            result.put(Constants.TOKEN, webToken);
        } catch (AuthenticationException e) {
            log.error("用户名或密码不正确！", e);
            result = AjaxResult.error(HttpStatus.ERROR, "用户名或密码不正确！");
        }
        return result;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("login/getInfo")
    public AjaxResult getInfo() {
        Subject subject = SecurityUtils.getSubject();
        ActiverUser activerUser = (ActiverUser) subject.getPrincipal();
        AjaxResult ajax = AjaxResult.success();
        ajax.put("username", activerUser.getUser().getUserName());
        ajax.put("picture", activerUser.getUser().getPicture());
        ajax.put("roles", activerUser.getRoles());
        ajax.put("permissions", activerUser.getPermissions());
        return ajax;
    }

    /**
     * 用户退出
     */
    @GetMapping("login/logout")
    public AjaxResult logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return AjaxResult.success("用户退出成功");
    }

    /**
     * 获取应该显示的菜单信息
     *
     * @return 菜单信息
     */
    @GetMapping("login/getMenus")
    public AjaxResult getMeuns() {
        // 获取登录的用户activerUser
        Subject subject = SecurityUtils.getSubject();
        ActiverUser activerUser = (ActiverUser) subject.getPrincipal();
        // 判断用户类型是否是管理员
        boolean isAdmin = activerUser.getUser().getUserType().equals(Constants.USER_ADMIN);
        SimpleUser simpleUser = null;
        if (!isAdmin) {
            // 构造登录用户的传输对象
            simpleUser = new SimpleUser(activerUser.getUser().getUserId(), activerUser.getUser().getUserName());
        }
        List<Menu> menus = menuService.selectMenuTree(isAdmin, simpleUser);
        ArrayList<MenuTreeVo> menuTreeVos = new ArrayList<>();
        for (Menu menu : menus) {
            menuTreeVos.add(new MenuTreeVo(menu.getMenuId().toString(), menu.getPath()));
        }
        return AjaxResult.success(menuTreeVos);
    }

}
