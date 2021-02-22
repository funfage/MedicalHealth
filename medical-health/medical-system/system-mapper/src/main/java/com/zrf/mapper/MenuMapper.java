package com.zrf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zrf.domain.Menu;

public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 根据菜单ID查询它的子节点个数
     *
     * @param menuId
     * @return
     */
    Long queryChildCountByMenuId(Long menuId);
}