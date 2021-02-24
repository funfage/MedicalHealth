package com.zrf.service;

import com.zrf.domain.RegisteredItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zrf.dto.RegisteredItemDto;
import com.zrf.vo.DataGridView;

import java.util.List;

public interface RegisteredItemService {
    /**
     * 分页查询
     *
     * @param registeredItemDto
     * @return
     */
    DataGridView listRegisteredItemPage(RegisteredItemDto registeredItemDto);

    /**
     * 根据ID查询
     *
     * @param registeredItemId
     * @return
     */
    RegisteredItem getOne(Long registeredItemId);

    /**
     * 添加
     *
     * @param registeredItemDto
     * @return
     */
    int addRegisteredItem(RegisteredItemDto registeredItemDto);

    /**
     * 修改
     *
     * @param registeredItemDto
     * @return
     */
    int updateRegisteredItem(RegisteredItemDto registeredItemDto);

    /**
     * 根据ID删除
     *
     * @param registeredItemIds
     * @return
     */
    int deleteRegisteredItemByIds(Long[] registeredItemIds);

    /**
     * 查询所有可用的挂号项目
     * @return
     */
    List<RegisteredItem> queryAllRegisteredItems();

}
