package com.zrf.controller.system;

import cn.hutool.core.date.DateUtil;
import com.zrf.constants.Constants;
import com.zrf.constants.HttpStatus;
import com.zrf.domain.LoginInfo;
import com.zrf.domain.Menu;
import com.zrf.domain.SimpleUser;
import com.zrf.dto.LoginBodyDto;
import com.zrf.service.LoginInfoService;
import com.zrf.service.MenuService;
import com.zrf.utils.AddressUtils;
import com.zrf.utils.IpUtils;
import com.zrf.utils.ShiroSecurityUtils;
import com.zrf.vo.ActiverUser;
import com.zrf.vo.AjaxResult;
import com.zrf.vo.MenuTreeVo;
import eu.bitwalker.useragentutils.UserAgent;
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

    @Autowired
    private LoginInfoService loginInfoService;

    /**
     * 登录方法
     *
     * @param loginBodyDto
     * @param request
     * @return
     */
    @PostMapping("login/doLogin")
    public AjaxResult login(@RequestBody @Validated LoginBodyDto loginBodyDto, HttpServletRequest request) {
        AjaxResult result = AjaxResult.success();
        String username = loginBodyDto.getUsername();
        String password = loginBodyDto.getPassword();
        // 构造用户名和密码的token
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        // 封装用户登录信息
        LoginInfo loginInfo = createLoginInfo(request);
        loginInfo.setLoginAccount(loginBodyDto.getUsername());
        try {
            // 调用shiro的登录进行认证
            subject.login(token);
            // 得到会话的token，也就是redis里存的
            Serializable webToken = subject.getSession().getId();
            result.put(Constants.TOKEN, webToken);
            // 设置登录成功的日志信息
            loginInfo.setLoginStatus(Constants.LOGIN_SUCCESS);
            loginInfo.setMsg("登录成功");
            loginInfo.setUserName(ShiroSecurityUtils.getCurrentUserName());
        } catch (AuthenticationException e) {
            log.error("用户名或密码不正确！", e);
            result = AjaxResult.error(HttpStatus.ERROR, "用户名或密码不正确！");
            loginInfo.setLoginStatus(Constants.LOGIN_ERROR);
            loginInfo.setMsg("用户名或密码不正确");
        }
        // 保存登录日志信息
        loginInfoService.insertLoginInfo(loginInfo);
        return result;
    }
    /**
     * 得到用户的登陆信息
     * @param request
     * @return
     */
    private LoginInfo createLoginInfo(HttpServletRequest request) {
        LoginInfo loginInfo = new LoginInfo();
        UserAgent userAgent = new UserAgent(request.getHeader("User-Agent"));
        String ipAddr = IpUtils.getIpAddr(request);
        String location = AddressUtils.getRealAddressByIP(ipAddr);
        // 获取客户端操作系统
        String os = userAgent.getOperatingSystem().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        loginInfo.setIpAddr(ipAddr);
        loginInfo.setLoginLocation(location);
        loginInfo.setOs(os);
        loginInfo.setBrowser(browser);
        loginInfo.setLoginTime(DateUtil.date());
        loginInfo.setLoginType(Constants.LOGIN_TYPE_SYSTEM);
        return loginInfo;
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
