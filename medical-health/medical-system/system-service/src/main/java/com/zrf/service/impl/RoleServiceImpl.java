package com.zrf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrf.constants.Constants;
import com.zrf.domain.Role;
import com.zrf.dto.RoleDto;
import com.zrf.mapper.RoleMapper;
import com.zrf.service.RoleService;
import com.zrf.vo.DataGridView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@Service
public class RoleServiceImpl implements RoleService{

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public DataGridView listRolePage(RoleDto roleDto) {
        Page<Role> page=new Page<>(roleDto.getPageNum(), roleDto.getPageSize());
        QueryWrapper<Role> wrapper=new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(roleDto.getRoleName()), Role.COL_ROLE_NAME, roleDto.getRoleName())
        .like(StringUtils.isNotBlank(roleDto.getRoleCode()), Role.COL_ROLE_CODE, roleDto.getRoleCode())
        .eq(StringUtils.isNotBlank(roleDto.getStatus()), Role.COL_STATUS, roleDto.getStatus())
        .ge(null!= roleDto.getBeginTime(), Role.COL_CREATE_TIME, roleDto.getBeginTime())
        .le(null!= roleDto.getEndTime(), Role.COL_CREATE_TIME, roleDto.getEndTime())
        .orderByAsc(Role.COL_ROLE_SORT);
        roleMapper.selectPage(page,wrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    @Override
    public List<Role> listAllRoles() {
        QueryWrapper<Role> wrapper=new QueryWrapper<>();
        wrapper.eq(Role.COL_STATUS, Constants.STATUS_TRUE);
        wrapper.orderByAsc(Role.COL_ROLE_SORT);
        return roleMapper.selectList(wrapper);
    }

    @Override
    public Role getOne(Long roleId) {
        return roleMapper.selectById(roleId);
    }

    @Override
    public int addRole(RoleDto roleDto) {
        Role role =new Role();
        BeanUtil.copyProperties(roleDto,role);
        //设置创建人和创建时间
        role.setCreateBy(roleDto.getSimpleUser().getUserName());
        role.setCreateTime(DateUtil.date());
        return roleMapper.insert(role);
    }

    @Override
    public int updateRole(RoleDto roleDto) {
        Role role =new Role();
        BeanUtil.copyProperties(roleDto,role);
        //设置修改人
        role.setUpdateBy(roleDto.getSimpleUser().getUserName());
        return roleMapper.updateById(role);
    }

    @Override
    public int deleteRoleByIds(Long[] roleIds) {
        List<Long> ids = Arrays.asList(roleIds);
        if (ids.size() > 0) {
            // 删除用户菜单关联表
            this.roleMapper.deleteRoleMenuByRoleIds(ids);
            // 还要删除用户角色关联表
            this.roleMapper.deleteRoleUserByRoleIds(ids);
            return this.roleMapper.deleteBatchIds(ids);
        }
        return 0;
    }

    @Override
    public void saveRoleMenu(Long roleId, Long[] menuIds) {
        //清空原有的数据
        roleMapper.deleteRoleMenuByRoleIds(Arrays.asList(roleId));
        for (Long menuId : menuIds) {
            roleMapper.saveRoleMenu(roleId, menuId);
        }
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        if (null == userId) {
            return Collections.EMPTY_LIST;
        }
        return roleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    public void saveRoleUser(Long userId, Long[] roleIds) {
        roleMapper.deleteRoleUserByUserIds(Arrays.asList(userId));
        for (Long roleId : roleIds) {
            roleMapper.saveRoleUser(userId, roleId);
        }
    }
}
