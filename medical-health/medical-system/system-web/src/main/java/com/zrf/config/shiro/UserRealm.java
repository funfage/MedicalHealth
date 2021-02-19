package com.zrf.config.shiro;

import com.zrf.domain.User;
import com.zrf.service.UserService;
import com.zrf.vo.ActiverUser;
import jdk.nashorn.internal.parser.Token;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 张润发
 * 自定义realm去匹配用户名和密码
 * @date 2021/2/18
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 做认证 --就是登录
     *
     * @param token
     * @return
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //得到用户名登录
        String phone = token.getPrincipal().toString();
        //根据用户电话查询用户是否存在
        User user = userService.queryUserByPhone(phone);
        if (null != user) {
            //组装存放到redis里面的对象
            ActiverUser activerUser = new ActiverUser();
            activerUser.setUser(user);
            // 匹配密码
            return new SimpleAuthenticationInfo(activerUser, user.getPassword(), ByteSource.Util.bytes(user.getSalt()), this.getName());
        } else { // 代表用户不存在
            return null;
        }
    }

    /**
     * 做授权 --登录成功之后判断用户是否某个菜单或按钮的权限
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 身份得到的就是上一个方法返回的值的第一个参数activeruser
        ActiverUser activerUser = (ActiverUser) principals.getPrimaryPrincipal();
        return new SimpleAuthorizationInfo();
    }


}
