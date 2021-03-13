package com.zrf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zrf.constants.Constants;
import com.zrf.domain.*;
import com.zrf.dto.CareHistoryDto;
import com.zrf.dto.CareOrderDto;
import com.zrf.dto.CareOrderFormDto;
import com.zrf.dto.CareOrderItemDto;
import com.zrf.mapper.*;
import com.zrf.service.CareService;
import com.zrf.service.MedicinesService;
import com.zrf.utils.IdGeneratorSnowflake;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/6
 */
@Service
public class CareServiceImpl implements CareService {

    @Autowired
    private CareHistoryMapper careHistoryMapper;

    @Autowired
    private CareOrderMapper careOrderMapper;

    @Autowired
    private CareOrderItemMapper careOrderItemMapper;

    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private OrderChargeItemMapper orderChargeItemMapper;

    @Reference
    private MedicinesService medicinesService;

    @Override
    public List<CareHistory> queryCareHistoryByPatientId(String patientId) {
        QueryWrapper<CareHistory> qw = new QueryWrapper<>();
        qw.eq(CareHistory.COL_PATIENT_ID, patientId);
        return careHistoryMapper.selectList(qw);
    }

    @Override
    public CareHistory saveOrUpdateCareHistory(CareHistoryDto careHistoryDto) {
        CareHistory careHistory = new CareHistory();
        BeanUtil.copyProperties(careHistoryDto, careHistory);
        // 如果病历id不为空则进行修改，否则进行添加
        if (StringUtils.isNotBlank(careHistoryDto.getChId())) {
            careHistoryMapper.updateById(careHistory);
        } else {
            // 随机生成病历编号
            careHistory.setChId(IdGeneratorSnowflake.generatorIdWithProfix(Constants.ID_PROFIX_CH));
            careHistoryMapper.insert(careHistory);
        }
        return careHistory;
    }

    @Override
    public CareHistory queryCareHistoryByRegId(String regId) {
        QueryWrapper<CareHistory> wrapper = new QueryWrapper<>();
        wrapper.eq(CareHistory.COL_REG_ID, regId);
        return careHistoryMapper.selectOne(wrapper);
    }

    @Override
    public List<CareOrder> queryCareOrdersByChId(String chId) {
        QueryWrapper<CareOrder> qw = new QueryWrapper<>();
        qw.eq(CareOrder.COL_CH_ID, chId);
        return careOrderMapper.selectList(qw);
    }

    @Override
    public List<CareOrderItem> queryCareOrderItemsByCoId(String coId, String status) {
        QueryWrapper<CareOrderItem> qw = new QueryWrapper<>();
        qw.eq(CareOrderItem.COL_CO_ID, coId);
        qw.eq(StringUtils.isNotBlank(status), CareOrderItem.COL_STATUS, status);
        return careOrderItemMapper.selectList(qw);
    }

    @Override
    public CareHistory queryCareHistoryByChId(String chId) {
        return careHistoryMapper.selectById(chId);
    }

    @Override
    public int saveCareOrderItem(CareOrderFormDto careOrderFormDto) {
        CareOrderDto careOrderDto = careOrderFormDto.getCareOrder();
        CareOrder careOrder = new CareOrder();
        BeanUtil.copyProperties(careOrderDto, careOrder);
        careOrder.setCreateBy(careOrderDto.getSimpleUser().getUserName());
        careOrder.setCreateTime(DateUtil.date());
        // 保存处方主表
        int insert = careOrderMapper.insert(careOrder);
        List<CareOrderItemDto> careOrderItems = careOrderFormDto.getCareOrderItems();
        // 保存处方详情
        for (CareOrderItemDto careOrderItemDto : careOrderItems) {
            CareOrderItem careOrderItem = new CareOrderItem();
            BeanUtil.copyProperties(careOrderItemDto, careOrderItem);
            // 生成处方详情id
            careOrderItem.setItemId(IdGeneratorSnowflake.generatorIdWithProfix(Constants.ID_PROFIX_ITEM));
            // 设置处方id
            careOrderItem.setCoId(careOrder.getCoId());
            careOrderItem.setCreateTime(DateUtil.date());
            //未支付
            careOrderItem.setStatus(Constants.ORDER_DETAILS_STATUS_0);
            careOrderItemMapper.insert(careOrderItem);
        }
        return insert;
    }

    @Override
    public CareOrderItem queryCareOrderItemByItemId(String itemId) {
        return careOrderItemMapper.selectById(itemId);
    }

    @Override
    public int deleteCareOrderItemByItemId(String itemId) {
        //注意点，如果删除了，要更新careOrder主表的all_amount
        CareOrderItem careOrderItem = careOrderItemMapper.selectById(itemId);
        String coId = careOrderItem.getCoId();//取出主表ID
        //删除
        int i = careOrderItemMapper.deleteById(itemId);
        //再根据coID查询还存在的详情数据
        QueryWrapper<CareOrderItem> qw = new QueryWrapper<>();
        qw.eq(CareOrderItem.COL_CO_ID, coId);
        List<CareOrderItem> careOrderItems = careOrderItemMapper.selectList(qw);
        if (careOrderItems != null && careOrderItems.size() > 0) {
            //重新计算总价格
            BigDecimal allAmount = new BigDecimal("0");
            for (CareOrderItem orderItem : careOrderItems) {
                allAmount = allAmount.add(orderItem.getAmount());
            }
            //再根据coId查询主表的数据
            CareOrder careOrder = careOrderMapper.selectById(coId);
            //更新主表的数据
            careOrder.setAllAmount(allAmount);
            careOrderMapper.updateById(careOrder);
        } else {
            //说明没有详情了，直接干掉主表里面的数据
            careOrderMapper.deleteById(coId);
        }
        return i;
    }

    @Override
    public int visitComplete(String regId) {
        Registration registration = new Registration();
        registration.setRegId(regId);
        registration.setRegStatus(Constants.REG_STATUS_3);
        return registrationMapper.updateById(registration);
    }

    @Override
    public String doMedicine(List<String> itemIds) {
        //根据详情ID查询处方详情
        QueryWrapper<CareOrderItem> qw=new QueryWrapper<>();
        qw.in(CareOrderItem.COL_ITEM_ID,itemIds);
        List<CareOrderItem> careOrderItems = this.careOrderItemMapper.selectList(qw);
        StringBuffer sb=new StringBuffer();
        for (CareOrderItem careOrderItem : careOrderItems) {
            //库存扣减
            int i=this.medicinesService.deductionMedicinesStorage(Long.valueOf(careOrderItem.getItemRefId()),careOrderItem.getNum().longValue());
            if(i>0){//说明库存够
                //更新处方详情状态
                careOrderItem.setStatus(Constants.ORDER_DETAILS_STATUS_3);//已完成
                this.careOrderItemMapper.updateById(careOrderItem);
                //更新收费详情状态
                OrderChargeItem orderChargeItem=new OrderChargeItem();
                orderChargeItem.setItemId(careOrderItem.getItemId());
                orderChargeItem.setStatus(Constants.ORDER_DETAILS_STATUS_3);
                this.orderChargeItemMapper.updateById(orderChargeItem);
            }else{
                sb.append("【").append(careOrderItem.getItemName()).append("】发药失败\n");
            }
        }
        if(StringUtils.isBlank(sb.toString())){
            return null;
        }else{
            sb.append("原因：库存不足");
            return sb.toString();
        }
    }

    @Override
    public List<CareOrderItem> queryCareOrderItemsByStatus(String coType, String status) {
        QueryWrapper<CareOrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(coType), CareOrderItem.COL_ITEM_TYPE, coType);
        wrapper.eq(StringUtils.isNotBlank(status), CareOrderItem.COL_STATUS, status);
        return careOrderItemMapper.selectList(wrapper);
    }

    @Override
    public CareOrder queryCareOrderByCoId(String coId) {
        return careOrderMapper.selectById(coId);
    }
}
