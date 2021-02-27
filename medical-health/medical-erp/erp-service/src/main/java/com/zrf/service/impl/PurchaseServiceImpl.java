package com.zrf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrf.constants.Constants;
import com.zrf.domain.*;
import com.zrf.dto.PurchaseDto;
import com.zrf.dto.PurchaseFormDto;
import com.zrf.dto.PurchaseItemDto;
import com.zrf.mapper.InventoryLogMapper;
import com.zrf.mapper.MedicinesMapper;
import com.zrf.mapper.PurchaseItemMapper;
import com.zrf.mapper.PurchaseMapper;
import com.zrf.service.PurchaseService;
import com.zrf.utils.IdGeneratorSnowflake;
import com.zrf.vo.DataGridView;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service(methods = {
        @Method(name = "doAudit", retries = 0),
        @Method(name = "addPurchaseAndItem", retries = 0), @Method(name = "addPurchaseAndItemToAudit", retries = 0),
        @Method(name = "doInventory", retries = 0)})
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseMapper purchaseMapper;

    @Autowired
    private PurchaseItemMapper purchaseItemMapper;

    @Autowired
    private InventoryLogMapper inventoryLogMapper;

    @Autowired
    private MedicinesMapper medicinesMapper;

    @Override
    public DataGridView listPurchasePage(PurchaseDto purchaseDto) {
        Page<Purchase> page = new Page<>(purchaseDto.getPageNum(), purchaseDto.getPageSize());
        QueryWrapper<Purchase> qw = new QueryWrapper<>();
        qw.eq(StringUtils.isNotBlank(purchaseDto.getProviderId()), Purchase.COL_PROVIDER_ID, purchaseDto.getProviderId());
        qw.eq(StringUtils.isNotBlank(purchaseDto.getStatus()), Purchase.COL_STATUS, purchaseDto.getStatus());
        qw.like(StringUtils.isNotBlank(purchaseDto.getApplyUserName()), Purchase.COL_APPLY_USER_NAME, purchaseDto.getApplyUserName());
        qw.orderByDesc(Purchase.COL_CREATE_TIME);
        purchaseMapper.selectPage(page, qw);
        return new DataGridView(page.getTotal(), page.getRecords());
    }

    @Override
    public Purchase getPurchaseById(String purchaseId) {
        return purchaseMapper.selectById(purchaseId);
    }

    @Override
    public int doAudit(String purchaseId, SimpleUser simpleUser) {
        Purchase purchase = purchaseMapper.selectById(purchaseId);
        //设置状态为待审核
        purchase.setStatus(Constants.STOCK_PURCHASE_STATUS_2);
        purchase.setApplyUserName(simpleUser.getUserName());
        purchase.setApplyUserId(Long.valueOf(simpleUser.getUserId().toString()));
        return purchaseMapper.updateById(purchase);
    }

    @Override
    public int doInvalid(String purchaseId) {
        Purchase purchase = this.purchaseMapper.selectById(purchaseId);
        //设置状态为作废
        purchase.setStatus(Constants.STOCK_PURCHASE_STATUS_5);
        return this.purchaseMapper.updateById(purchase);
    }

    @Override
    public int auditPass(String purchaseId) {
        Purchase purchase = this.purchaseMapper.selectById(purchaseId);
        //设置状态为审核通过
        purchase.setStatus(Constants.STOCK_PURCHASE_STATUS_3);
        return this.purchaseMapper.updateById(purchase);
    }

    @Override
    public int auditNoPass(String purchaseId, String auditMsg) {
        Purchase purchase = this.purchaseMapper.selectById(purchaseId);
        //设置状态为审核不通过
        purchase.setStatus(Constants.STOCK_PURCHASE_STATUS_4);
        purchase.setAuditMsg(auditMsg);
        return this.purchaseMapper.updateById(purchase);
    }

    @Override
    public List<PurchaseItem> getPurchaseItemById(String purchaseId) {
        if (null != purchaseId) {
            QueryWrapper<PurchaseItem> qw = new QueryWrapper<>();
            qw.eq(PurchaseItem.COL_PURCHASE_ID, purchaseId);
            return purchaseItemMapper.selectList(qw);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 暂存   采购单的状态是Constants.STOCK_PURCHASE_STATUS_1
     *
     * @param purchaseFormDto
     * @return
     */
    @Override
    public int addPurchaseAndItem(PurchaseFormDto purchaseFormDto) {
        Purchase purchase = purchaseMapper.selectById(purchaseFormDto.getPurchaseDto().getPurchaseId());
        if (null != purchase) {
            // 删除之前的数据
            purchaseMapper.deleteById(purchase.getPurchaseId());
            QueryWrapper<PurchaseItem> wrapper = new QueryWrapper<>();
            wrapper.eq(PurchaseItem.COL_PURCHASE_ID, purchase.getPurchaseId());
            purchaseItemMapper.delete(wrapper);
        }
        Purchase newPurchase = new Purchase();
        BeanUtil.copyProperties(purchaseFormDto.getPurchaseDto(), newPurchase);
        // 未提交状态
        newPurchase.setStatus(Constants.STOCK_PURCHASE_STATUS_1);
        newPurchase.setCreateBy(purchaseFormDto.getPurchaseDto().getSimpleUser().getUserName());
        newPurchase.setCreateTime(DateUtil.date());
        // 保存采购详情数据
        int insert = purchaseMapper.insert(newPurchase);
        for (PurchaseItemDto item : purchaseFormDto.getPurchaseItemDtos()) {
            PurchaseItem purchaseItem = new PurchaseItem();
            BeanUtil.copyProperties(item, purchaseItem);
            purchaseItem.setPurchaseId(newPurchase.getPurchaseId());
            purchaseItem.setItemId(IdGeneratorSnowflake.snowflakeId().toString());
            purchaseItemMapper.insert(purchaseItem);
        }
        return insert;
    }

    /**
     * 保存并提交审核  状态应该是Constants.STOCK_PURCHASE_STATUS_2
     *
     * @param purchaseFormDto
     * @return
     */
    @Override
    public int addPurchaseAndItemToAudit(PurchaseFormDto purchaseFormDto) {
        Purchase purchase = purchaseMapper.selectById(purchaseFormDto.getPurchaseDto().getPurchaseId());
        if (null != purchase) {
            // 删除之前的数据
            purchaseMapper.deleteById(purchase.getPurchaseId());
            QueryWrapper<PurchaseItem> wrapper = new QueryWrapper<>();
            wrapper.eq(PurchaseItem.COL_PURCHASE_ID, purchase.getPurchaseId());
            purchaseItemMapper.delete(wrapper);
        }
        Purchase newPurchase = new Purchase();
        BeanUtil.copyProperties(purchaseFormDto.getPurchaseDto(), newPurchase);
        // 待审核状态
        newPurchase.setStatus(Constants.STOCK_PURCHASE_STATUS_2);
        newPurchase.setCreateBy(purchaseFormDto.getPurchaseDto().getSimpleUser().getUserName());
        newPurchase.setCreateTime(DateUtil.date());
        // 设置申请人和申请人的id
        newPurchase.setApplyUserId(Long.valueOf(purchaseFormDto.getPurchaseDto().getSimpleUser().getUserId().toString()));
        newPurchase.setApplyUserName(purchaseFormDto.getPurchaseDto().getSimpleUser().getUserName());
        // 保存采购详情数据
        int insert = purchaseMapper.insert(newPurchase);
        for (PurchaseItemDto item : purchaseFormDto.getPurchaseItemDtos()) {
            PurchaseItem purchaseItem = new PurchaseItem();
            BeanUtil.copyProperties(item, purchaseItem);
            purchaseItem.setPurchaseId(newPurchase.getPurchaseId());
            purchaseItem.setItemId(IdGeneratorSnowflake.snowflakeId().toString());
            purchaseItemMapper.insert(purchaseItem);
        }
        return insert;
    }

    /**
     * 入库
     * 逻辑
     * 向stock_inventory_log表里面添加数据 并更新stock_medicines的库存
     *
     * @param purchaseId
     * @param currentSimpleUser
     * @return
     */
    @Override
    public int doInventory(String purchaseId, SimpleUser currentSimpleUser) {
        Purchase purchase = purchaseMapper.selectById(purchaseId);
        // 只有状态为审核成功才能入库
        if (null != purchase || purchase.getStatus().equals(Constants.STOCK_PURCHASE_STATUS_3)) {
            //查询详情
            QueryWrapper<PurchaseItem> qw = new QueryWrapper<>();
            qw.eq(PurchaseItem.COL_PURCHASE_ID, purchase.getPurchaseId());
            List<PurchaseItem> purchaseItems = this.purchaseItemMapper.selectList(qw);
            for (PurchaseItem purchaseItem : purchaseItems) {
                InventoryLog inventoryLog = new InventoryLog();
                inventoryLog.setInventoryLogId(purchaseItem.getItemId());
                inventoryLog.setPurchaseId(purchaseItem.getPurchaseId());
                inventoryLog.setMedicinesId(purchaseItem.getMedicinesId());
                inventoryLog.setInventoryLogNum(purchaseItem.getPurchaseNumber());
                inventoryLog.setTradePrice(purchaseItem.getTradePrice());
                inventoryLog.setTradeTotalAmount(purchaseItem.getTradeTotalAmount());
                inventoryLog.setBatchNumber(purchaseItem.getBatchNumber());
                inventoryLog.setMedicinesName(purchaseItem.getMedicinesName());
                inventoryLog.setMedicinesType(purchaseItem.getMedicinesType());
                inventoryLog.setPrescriptionType(purchaseItem.getPrescriptionType());
                inventoryLog.setProducterId(purchaseItem.getProducterId());
                inventoryLog.setConversion(purchaseItem.getConversion());
                inventoryLog.setUnit(purchaseItem.getUnit());
                inventoryLog.setCreateTime(DateUtil.date());
                inventoryLog.setCreateBy(currentSimpleUser.getUserName());
                //保存数据
                inventoryLogMapper.insert(inventoryLog);
                //更新药品库存
                Medicines medicines = this.medicinesMapper.selectById(purchaseItem.getMedicinesId());
                medicines.setMedicinesStockNum(medicines.getMedicinesStockNum() + purchaseItem.getPurchaseNumber());
                medicines.setUpdateBy(currentSimpleUser.getUserName());
                this.medicinesMapper.updateById(medicines);
            }
            //入库成功  修改单据的状态为入库成功
            purchase.setStatus(Constants.STOCK_PURCHASE_STATUS_6);
            purchase.setStorageOptTime(DateUtil.date());
            purchase.setStorageOptUser(currentSimpleUser.getUserName());
            return this.purchaseMapper.updateById(purchase);
        }
        return -1;
    }
}
