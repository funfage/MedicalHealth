package com.zrf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zrf.domain.Menu;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据菜单ID查询它的子节点个数
     *
     * @param menuId
     * @return
     */
    Long queryChildCountByMenuId(Long menuId);

    /**
     * 根据角色ID查询所有选中的权限菜单ID【只查子节点的】
     *
     * @param roleId
     * @return
     */
    List<Long> queryMenuIdsByRoleId(Long roleId);

    /**
     * 根据用户id查询用户菜单
     *
     * @param userId
     * @return
     */
    List<Menu> selectListByUserId(@Param("userId") Serializable userId);
}