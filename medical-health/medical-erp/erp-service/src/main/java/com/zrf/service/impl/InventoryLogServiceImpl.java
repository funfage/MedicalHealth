package com.zrf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrf.domain.InventoryLog;
import com.zrf.dto.InventoryLogDto;
import com.zrf.mapper.InventoryLogMapper;
import com.zrf.service.InventoryLogService;
import com.zrf.vo.DataGridView;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 张润发
 * @date 2021/2/27
 */
@Service
public class InventoryLogServiceImpl implements InventoryLogService {

    @Autowired
    private InventoryLogMapper inventoryLogMapper;

    @Override
    public DataGridView listInventoryLogPage(InventoryLogDto inventoryLogDto) {
        Page<InventoryLog> page=new Page<>(inventoryLogDto.getPageNum(),inventoryLogDto.getPageSize());
        QueryWrapper<InventoryLog> qw=new QueryWrapper<>();
        qw.like(StringUtils.isNotBlank(inventoryLogDto.getPurchaseId()),InventoryLog.COL_PURCHASE_ID,inventoryLogDto.getPurchaseId());
        qw.like(StringUtils.isNotBlank(inventoryLogDto.getMedicinesName()),InventoryLog.COL_MEDICINES_NAME,inventoryLogDto.getMedicinesName());
        qw.eq(StringUtils.isNotBlank(inventoryLogDto.getMedicinesType()),InventoryLog.COL_MEDICINES_TYPE,inventoryLogDto.getMedicinesType());
        qw.eq(StringUtils.isNotBlank(inventoryLogDto.getPrescriptionType()),InventoryLog.COL_PRESCRIPTION_TYPE,inventoryLogDto.getPrescriptionType());
        qw.eq(StringUtils.isNotBlank(inventoryLogDto.getProducterId()),InventoryLog.COL_PRODUCTER_ID,inventoryLogDto.getProducterId());
        qw.eq(StringUtils.isNotBlank(inventoryLogDto.getPrescriptionType()),InventoryLog.COL_PRESCRIPTION_TYPE,inventoryLogDto.getPrescriptionType());

        qw.ge(inventoryLogDto.getBeginTime()!=null,InventoryLog.COL_CREATE_TIME,inventoryLogDto.getBeginTime());
        qw.le(inventoryLogDto.getEndTime()!=null,InventoryLog.COL_CREATE_TIME,inventoryLogDto.getEndTime());
        qw.orderByDesc(InventoryLog.COL_CREATE_TIME);
        inventoryLogMapper.selectPage(page,qw);
        return new DataGridView(page.getTotal(),page.getRecords());
    }
}
