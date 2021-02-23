package com.zrf.service;

import com.zrf.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zrf.dto.UserDto;
import com.zrf.vo.DataGridView;

import java.util.List;

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

    /**
     * 分页查询用户
     * @param userDto
     * @return
     */
    DataGridView listUserForPage(UserDto userDto);

    /**
     * 添加用户
     * @param userDto
     * @return
     */
    int addUser(UserDto userDto);

    /**
     * 修改用户
     * @param userDto
     * @return
     */
    int updateUser(UserDto userDto);

    /**
     * 删除用户
     * @param userIds
     * @return
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 查询所有可用的用户
     * @return
     */
    List<User> getAllUsers();

    /**
     * 重置用户密码
     *
     * @param userIds
     */
    void resetPassWord(Long[] userIds);
}
