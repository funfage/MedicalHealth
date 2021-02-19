package com.zrf.service;

import com.zrf.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserService {
    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return
     */
    User queryUserByPhone(String phone);

    /**
     * 根据用户ID查询用户
     * @param userId 用户编号
     * @return
     */
    User getOne(Long userId);


}
