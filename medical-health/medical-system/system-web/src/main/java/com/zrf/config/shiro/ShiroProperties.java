package com.zrf.config.shiro;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 张润发
 * @date 2021/2/18
 */
@ConfigurationProperties(prefix = "shiro")
@Data
public class ShiroProperties {

    /**
     * 密码加密方式
     */
    private String hashAlgorithmName = "md5";
    /**
     * 密码散列次数
     */
    private Integer hashIterations = 2;

    /**
     * 不拦击的路径
     */
    private String[] anonUrls;

    /**
     * 拦截的路径
     */
    private String[] authcUrls;

}
