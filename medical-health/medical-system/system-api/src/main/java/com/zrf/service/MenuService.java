package com.zrf.service;

import com.zrf.domain.Menu;
import com.zrf.domain.SimpleUser;

import java.util.List;

public interface MenuService {

    /**
     * 查询菜单信息
     * 如果用户是超级管理员，那么查询所有菜单和权限
     * 如果用户是普通用户，那么根据用户ID关联角色和权限
     *
     * @param isAdmin    是否是超级管理员
     * @param simpleUser 如果isAdmin为true，simpleuser可以为空
     * @return
     */
    public List<Menu> selectMenuTree(boolean isAdmin, SimpleUser simpleUser);

}
