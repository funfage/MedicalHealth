package com.zrf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zrf.constants.Constants;
import com.zrf.domain.SimpleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zrf.mapper.MenuMapper;
import com.zrf.domain.Menu;
import com.zrf.service.MenuService;
@Service
public class MenuServiceImpl implements MenuService{

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<Menu> selectMenuTree(boolean isAdmin, SimpleUser simpleUser) {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        // 有效状态
        wrapper.eq(Menu.COL_STATUS, Constants.STATUS_TRUE);
        // 菜单类型
        wrapper.in(Menu.COL_MENU_TYPE, Constants.MENU_TYPE_M, Constants.MENU_TYPE_C);
        wrapper.orderByAsc(Menu.COL_PARENT_ID);
        if (isAdmin) {
            return menuMapper.selectList(wrapper);
        } else {
            // TODO 根据用户id查询拥有的菜单信息，这里先暂时返回一样的数据
            return menuMapper.selectList(wrapper);
        }
    }
}
