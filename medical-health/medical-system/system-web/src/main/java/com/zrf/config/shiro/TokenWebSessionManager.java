package com.zrf.config.shiro;

import com.zrf.constants.Constants;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author 张润发
 * 如果没有token生成一个返回到前台
 * 如果有就从请求头里取出来
 * @date 2021/2/18
 */
@Configuration
public class TokenWebSessionManager extends DefaultWebSessionManager {
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        //从头里面得到请求TOKEN 如果不存在就生成一个
        String header = WebUtils.toHttp(request).getHeader(Constants.TOKEN);
        if (StringUtils.hasText(header)) {
            return header;
        }
        return UUID.randomUUID().toString();
    }
}
