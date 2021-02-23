package com.zrf.service;

import com.zrf.domain.Role;
import com.zrf.dto.RoleDto;
import com.zrf.vo.DataGridView;

import java.util.List;

public interface RoleService {

    /**
     * 分页查询角色
     * @param roleDto
     * @return
     */
    DataGridView listRolePage(RoleDto roleDto);

    /**
     * 查询所有可用角色
     * @return
     */
    List<Role> listAllRoles();

    /**
     * 根据ID查询角色
     * @param roleId
     * @return
     */
    Role getOne(Long roleId);

    /**
     * 添加一个角色
     * @param roleDto
     * @return
     */
    int addRole(RoleDto roleDto);

    /**
     * 修改角色
     * @param roleDto
     * @return
     */
    int updateRole(RoleDto roleDto);

    /**
     * 根据角色ID删除角色
     * @param roleIds
     * @return
     */
    int deleteRoleByIds(Long[] roleIds);

    /**
     * 保存角色和菜单之间的关系
     * @param roleId
     * @param menuIds
     */
    void saveRoleMenu(Long roleId, Long[] menuIds);

    /***
     * 根据用户id查询用户拥有的角色IDS
     * @param userId
     * @return
     */
    List getRoleIdsByUserId(Long userId);

    /**
     * 保存角色和用户的关系
     * @param userId
     * @param roleIds
     */
    void saveRoleUser(Long userId, Long[] roleIds);
}
