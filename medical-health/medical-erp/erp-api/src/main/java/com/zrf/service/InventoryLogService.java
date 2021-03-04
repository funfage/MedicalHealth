package com.zrf.service;

import com.zrf.dto.InventoryLogDto;
import com.zrf.vo.DataGridView;

/**
 * @author 张润发
 * @date 2021/2/27
 */
public interface InventoryLogService {

    /**
     * 分页查询
     *
     * @param inventoryLogDto
     * @return
     */
    DataGridView listInventoryLogPage(InventoryLogDto inventoryLogDto);

}
