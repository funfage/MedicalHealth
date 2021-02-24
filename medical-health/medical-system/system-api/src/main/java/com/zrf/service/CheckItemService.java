package com.zrf.service;

import com.zrf.domain.CheckItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zrf.dto.CheckItemDto;
import com.zrf.vo.DataGridView;

import java.util.List;

public interface CheckItemService {

    /**
     * 分页查询
     *
     * @param checkItemDto
     * @return
     */
    DataGridView listCheckItemPage(CheckItemDto checkItemDto);

    /**
     * 根据ID查询
     *
     * @param checkItemId
     * @return
     */
    CheckItem getOne(Long checkItemId);

    /**
     * 添加
     *
     * @param checkItemDto
     * @return
     */
    int addCheckItem(CheckItemDto checkItemDto);

    /**
     * 修改
     *
     * @param checkItemDto
     * @return
     */
    int updateCheckItem(CheckItemDto checkItemDto);

    /**
     * 根据ID删除
     *
     * @param checkItemIds
     * @return
     */
    int deleteCheckItemByIds(Long[] checkItemIds);

    /**
     * 查询所有可用的检查项目
     * @return
     */
    List<CheckItem> queryAllCheckItems();

}
