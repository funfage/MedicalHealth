package com.zrf.service;

import com.zrf.domain.OrderCharge;
import com.zrf.domain.OrderChargeItem;
import com.zrf.dto.OrderChargeDto;
import com.zrf.dto.OrderChargeFormDto;
import com.zrf.vo.DataGridView;

import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/8
 */
public interface OrderChargeService {

    /**
     * 保存订单及详情信息
     * @param orderChargeFormDto
     */
    void saveOrderAndItems(OrderChargeFormDto orderChargeFormDto);

    /**
     * 支付成功后更新订单状态
     * @param orderId
     * @param payPlatformId 平台交易id，使用支付宝支付时需要使用
     */
    void paySuccess(String orderId, String payPlatformId, String payType);

    /**
     * 根据订单id查询订单信息
     * @param orderId
     * @return
     */
    OrderCharge queryOrderChargeByOrderId(String orderId);

    /**
     * 根据orderid删除无用的数据
     * @param orderId
     */
    void deleteOrderChargeAndItemsByOrderId(String orderId);

    /**
     * 分页查询所有收费单
     * @param orderChargeDto
     * @return
     */
    DataGridView queryAllOrderChargeForPage(OrderChargeDto orderChargeDto);

    /**
     * 跟姐姐收费单id查询收费详情信息
     * @param orderId
     * @return
     */
    List<OrderChargeItem> queryOrderChargeItemByOrderId(String orderId);

    /**
     * 根据详情ID查询收费订单详情
     * @param itemId
     * @return
     */
    OrderChargeItem queryOrderChargeItemByItemId(String itemId);
}
