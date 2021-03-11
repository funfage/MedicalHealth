package com.zrf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zrf.constants.Constants;
import com.zrf.domain.CareOrderItem;
import com.zrf.domain.OrderCharge;
import com.zrf.domain.OrderChargeItem;
import com.zrf.dto.OrderChargeDto;
import com.zrf.dto.OrderChargeFormDto;
import com.zrf.dto.OrderChargeItemDto;
import com.zrf.mapper.CareOrderItemMapper;
import com.zrf.mapper.CareOrderMapper;
import com.zrf.mapper.OrderChargeItemMapper;
import com.zrf.mapper.OrderChargeMapper;
import com.zrf.service.OrderChargeService;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/8
 */
@Service(methods = {@Method(name = "saveOrderAndItems", retries = 1),
        @Method(name = "paySuccess", retries = 1)})
public class OrderChargeServiceImpl implements OrderChargeService {

    @Autowired
    private OrderChargeMapper orderChargeMapper;

    @Autowired
    private OrderChargeItemMapper orderChargeItemMapper;

    @Autowired
    private CareOrderItemMapper careOrderItemMapper;

    @Override
    public void saveOrderAndItems(OrderChargeFormDto orderChargeFormDto) {
        OrderChargeDto orderChargeDto = orderChargeFormDto.getOrderChargeDto();
        List<OrderChargeItemDto> orderChargeItemDtoList = orderChargeFormDto.getOrderChargeItemDtoList();
        // 保存订单信息
        OrderCharge orderCharge = new OrderCharge();
        BeanUtil.copyProperties(orderChargeDto, orderCharge);
        // 设置订单状态为未支付状态
        orderCharge.setOrderStatus(Constants.ORDER_STATUS_0);
        orderCharge.setCreateTime(DateUtil.date());
        orderCharge.setCreateBy(orderChargeFormDto.getSimpleUser().getUserName());
        orderChargeMapper.insert(orderCharge);
        // 首先将原来的订单详情信息删除
        List<String> itemIds = new ArrayList<>();
        for (OrderChargeItemDto orderChargeItemDto : orderChargeItemDtoList) {
            itemIds.add(orderChargeItemDto.getItemId());
        }
        QueryWrapper<OrderChargeItem> wrapper = new QueryWrapper<>();
        wrapper.in(OrderChargeItem.COL_ITEM_ID, itemIds);
        orderChargeItemMapper.delete(wrapper);
        // 保存订单详情
        for (OrderChargeItemDto orderChargeItemDto : orderChargeItemDtoList) {
            OrderChargeItem orderChargeItem = new OrderChargeItem();
            BeanUtil.copyProperties(orderChargeItemDto, orderChargeItem);
            // 关联订单id
            orderChargeItem.setOrderId(orderCharge.getOrderId());
            orderChargeItem.setStatus(Constants.ORDER_DETAILS_STATUS_0);
            orderChargeItemMapper.insert(orderChargeItem);
        }
    }

    @Override
    public void paySuccess(String orderId, String payPlatformId) {
        // 根据id查询订单信息
        OrderCharge orderCharge = orderChargeMapper.selectById(orderId);
        // 设置交易编号
        orderCharge.setPayPlatformId(payPlatformId);
        // 设置交易时间
        orderCharge.setPayTime(DateUtil.date());
        // 修改订单状态为已支付
        orderCharge.setOrderStatus(Constants.ORDER_STATUS_1);
        orderChargeMapper.updateById(orderCharge);
        // 根据订单id查询订单详情
        QueryWrapper<OrderChargeItem> wrapper = new QueryWrapper<>();
        wrapper.eq(OrderChargeItem.COL_ORDER_ID, orderId);
        List<OrderChargeItem> orderChargeItems = orderChargeItemMapper.selectList(wrapper);
        // 存放所有的订单详情id
        List<String> allItemIds = new ArrayList<>();
        for (OrderChargeItem orderChargeItem : orderChargeItems) {
            allItemIds.add(orderChargeItem.getItemId());
        }
        // 更新订单详情的收费状态
        OrderChargeItem orderChargeItem = new OrderChargeItem();
        // 设置为已支付状态
        orderChargeItem.setStatus(Constants.ORDER_DETAILS_STATUS_1);
        QueryWrapper<OrderChargeItem> orderItemQw = new QueryWrapper<>();
        orderItemQw.in(OrderChargeItem.COL_ITEM_ID, allItemIds);
        orderChargeItemMapper.update(orderChargeItem, orderItemQw);
        // 更新处方详情状态
        CareOrderItem careOrderItem = new CareOrderItem();
        careOrderItem.setStatus(Constants.ORDER_DETAILS_STATUS_1);
        QueryWrapper<CareOrderItem> careItemQw = new QueryWrapper<>();
        careItemQw.in(CareOrderItem.COL_ITEM_ID, allItemIds);
        careOrderItemMapper.update(careOrderItem, careItemQw);
    }

    @Override
    public OrderCharge queryOrderChargeByOrderId(String orderId) {
        return orderChargeMapper.selectById(orderId);
    }

    @Override
    public void deleteOrderChargeAndItemsByOrderId(String orderId) {
        // 删除订单信息
        orderChargeMapper.deleteById(orderId);
        // 删除订单详情未支付的信息
        QueryWrapper<OrderChargeItem> wrapper = new QueryWrapper<>();
        wrapper.eq(OrderChargeItem.COL_ORDER_ID, orderId);
        wrapper.eq(OrderChargeItem.COL_STATUS, Constants.ORDER_DETAILS_STATUS_0);
        orderChargeItemMapper.delete(wrapper);
    }

}
