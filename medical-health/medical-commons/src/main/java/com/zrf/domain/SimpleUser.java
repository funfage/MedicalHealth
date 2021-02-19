package com.zrf.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 张润发
 * @Description 当登录用户对象传输类
 * @date 2021/2/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUser implements Serializable {
    private Serializable userId;
    private String userName;
}
